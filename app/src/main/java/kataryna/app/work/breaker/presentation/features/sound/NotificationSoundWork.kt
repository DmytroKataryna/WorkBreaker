package kataryna.app.work.breaker.presentation.features.sound

import android.app.Notification
import android.content.Context
import android.media.MediaPlayer
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kataryna.app.work.breaker.R
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NotificationSoundWork(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())
        val result = when (inputData.getString(ACTION_KEY)) {
            ACTION_NEW_NOTIFICATION -> playMedia(R.raw.notification_sound)
            ACTION_TIME_FOR_BREAK -> playMedia(R.raw.work_break_sound)
            else -> false
        }
        return if (result) Result.success() else Result.failure()
    }

    private fun createNotification(): Notification {
        val channelId = applicationContext.getString(R.string.channel_id)
        return Notification.Builder(applicationContext, channelId)
            .setContentTitle(applicationContext.getString(R.string.notification_sound_title))
            .setSmallIcon(R.drawable.ic_sound_played)
            .setContentIntent(createPendingIntent(applicationContext))
            .build()
    }

    private suspend fun playMedia(notificationSound: Int): Boolean {
        Timber.d("Play of media has started")
        val result = suspendCoroutine { continuation ->
            with(MediaPlayer.create(applicationContext, notificationSound)) {
                setOnCompletionListener {
                    Timber.d("Play has been completed")
                    continuation.resume(true)
                }
                setOnErrorListener { _, _, _ ->
                    Timber.d("Error on playing sound.")
                    continuation.resume(false)
                    true
                }
                start()
            }
        }
        return result
    }

    private fun createPendingIntent(context: Context) = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.tasksFragment)
        .createPendingIntent()

    companion object {
        const val ACTION_NEW_NOTIFICATION = "action_new_notification"
        const val ACTION_TIME_FOR_BREAK = "action_time_for_break"
        const val ACTION_KEY = "action_key"

        private const val NOTIFICATION_ID = 98331
    }
}
