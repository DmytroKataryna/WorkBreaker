package kataryna.app.work.breaker.data.local

import androidx.annotation.VisibleForTesting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kataryna.app.work.breaker.data.sysTime.TimeRetriever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class LocalPhotoStorageImpl(
    private val dataStore: DataStore<Preferences>,
    private val systemTimeRetriever: TimeRetriever
) : LocalPhotoStorage {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val photoUrlKey = stringPreferencesKey(PHOTO_URL_KEY)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val userTasksKey = stringPreferencesKey(USER_TASKS_KEY)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val photoTimestampKey = longPreferencesKey(PHOTO_URL_LOAD_TIME)

    override suspend fun isPhotoUrlValid(): Boolean {
        return dataStore.data.map { preferences ->
            if (preferences[photoUrlKey] == null) {
                return@map false
            }

            val currentTime = systemTimeRetriever.getSystemTime()
            val photoTime = preferences[photoTimestampKey] ?: 0L
            val diff = currentTime - photoTime

            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            // in case photo was loaded 2min ago, no need to do it again
            return@map minutes <= PHOTO_TIME_EXPIRE_MIN
        }.first()
    }

    override suspend fun loadPhotoUrl(): String? {
        return dataStore.data.map { preferences ->
            preferences[photoUrlKey]
        }.first()
    }

    override suspend fun savePhotoUrl(url: String) {
        dataStore.edit { settings ->
            settings[photoUrlKey] = url
            settings[photoTimestampKey] = systemTimeRetriever.getSystemTime()
        }
    }

    override suspend fun saveUserTasks(text: String) {
        dataStore.edit { settings ->
            settings[userTasksKey] = text
        }
    }

    override suspend fun getUserTasks(): String? {
        return dataStore.data.map { preferences ->
            preferences[userTasksKey]
        }.first()
    }

    companion object {
        private const val PHOTO_URL_KEY = "photo_url"
        private const val PHOTO_URL_LOAD_TIME = "photo_time"
        private const val USER_TASKS_KEY = "user_tasks"

        private const val PHOTO_TIME_EXPIRE_MIN = 2
    }
}
