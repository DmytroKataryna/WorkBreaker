package kataryna.app.work.breaker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kataryna.app.work.breaker.data.sysTime.TimeRetriever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocalPhotoStorage(val context: Context, private val systemTimeRetriever: TimeRetriever) {

    private val photoUrlKey = stringPreferencesKey(PHOTO_URL_KEY)
    private val userTasksKey = stringPreferencesKey(USER_TASKS_KEY)
    private val photoTimestampKey = longPreferencesKey(PHOTO_URL_LOAD_TIME)

    suspend fun photoUrlValid(): Boolean {
        return context.dataStore.data.map { preferences ->
            if (preferences[photoUrlKey] == null) {
                return@map false
            }

            val currentTime = systemTimeRetriever.getSystemTime()
            val photoTime = preferences[photoTimestampKey] ?: 0L
            val diff = currentTime - photoTime

            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            return@map minutes <= 2
        }.first()
    }

    suspend fun loadPhotoUrl(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[photoUrlKey]
        }.first()
    }

    suspend fun savePhotoUrl(url: String) {
        context.dataStore.edit { settings ->
            settings[photoUrlKey] = url
            settings[photoTimestampKey] = systemTimeRetriever.getSystemTime()
        }
    }

    suspend fun saveUserTasks(text: String) {
        context.dataStore.edit { settings ->
            settings[userTasksKey] = text
        }
    }

    suspend fun getUserTasks(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[userTasksKey]
        }.first()
    }

    companion object {
        const val PHOTO_URL_KEY = "photo_url"
        const val PHOTO_URL_LOAD_TIME = "photo_time"
        const val USER_TASKS_KEY = "user_tasks"
    }
}