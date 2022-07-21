package kataryna.app.work.breaker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val id = getString(R.string.channel_id)
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance).apply {
            description = descriptionText
            enableVibration(true)
        }

        with((getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)) {
            createNotificationChannel(channel)
        }
    }
}
