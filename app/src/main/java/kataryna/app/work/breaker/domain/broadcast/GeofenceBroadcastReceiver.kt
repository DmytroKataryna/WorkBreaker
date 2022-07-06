package kataryna.app.work.breaker.domain.broadcast

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import kataryna.app.work.breaker.R
import kataryna.app.work.breaker.data.local.LocalLocationStorage
import kataryna.app.work.breaker.presentation.sound.NotificationSoundWork
import kataryna.app.work.breaker.utils.orZero
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sharedPrefs: LocalLocationStorage

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("GeofenceBroadcastReceiver received event")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            GeofenceStatusCodes.getStatusCodeString(geofencingEvent?.errorCode.orZero()).let {
                Timber.e(it)
            }
            return
        }
        val workManager = WorkManager.getInstance(context)
        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                val notificationTitle = context.getString(R.string.notification_enter_title)
                val notificationMsg = context.getString(R.string.notification_enter_txt)
                sendNotification(context, notificationTitle, notificationMsg)
                scheduleWorkBreakerAlarm(workManager)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val notificationTitle = context.getString(R.string.notification_leave_title)
                val notificationMsg = context.getString(R.string.notification_leave_text)
                sendNotification(context, notificationTitle, notificationMsg)
                cancelWorkBreakerAlarm(workManager)
                launchNewNotificationSound(workManager)
            }
            else -> Timber.w("Unknown geofence transition")
        }
    }

    private fun launchNewNotificationSound(mng: WorkManager) {
        OneTimeWorkRequestBuilder<NotificationSoundWork>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf(NotificationSoundWork.ACTION_KEY to NotificationSoundWork.ACTION_NEW_NOTIFICATION))
            .addTag(EXIT_ZONE_TAG)
            .build()
            .let { mng.enqueue(it) }
    }

    private fun scheduleWorkBreakerAlarm(mng: WorkManager) {
        PeriodicWorkRequestBuilder<NotificationSoundWork>(INTERVAL_IN_MIN, TimeUnit.MINUTES)
            .setInputData(workDataOf(NotificationSoundWork.ACTION_KEY to NotificationSoundWork.ACTION_TIME_FOR_BREAK))
            .setInitialDelay(INTERVAL_IN_MIN, TimeUnit.MINUTES)
            .addTag(WORK_TAG)
            .build()
            .let { mng.enqueue(it) }
    }

    private fun cancelWorkBreakerAlarm(mng: WorkManager) {
        mng.cancelAllWorkByTag(WORK_TAG)
    }

    private fun sendNotification(
        context: Context,
        notificationTitle: String,
        notificationText: String
    ) {
        val id = context.getString(R.string.channel_id)
        val iconLarge = BitmapFactory.decodeResource(context.resources, R.drawable.ic_doggy)
        val builder = NotificationCompat.Builder(context, id)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setLargeIcon(iconLarge)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(createPendingIntent(context))

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createPendingIntent(context: Context) = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.tasksFragment)
        .createPendingIntent()

    companion object {
        private const val NOTIFICATION_ID = 22231
        private const val WORK_TAG = "notification_sound_work"
        private const val EXIT_ZONE_TAG = "exit_zone_work"
        private const val INTERVAL_IN_MIN = 45L
    }
}