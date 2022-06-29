package kataryna.app.work.breaker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.utils.orZero
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location")

class LocalLocationStorage(val context: Context) {

    private val latitudeKey = doublePreferencesKey(LAT_KEY)
    private val longitudeKey = doublePreferencesKey(LONG_KEY)

    suspend fun saveLocation(loc: LatLng) {
        context.dataStore.edit { locationStorage ->
            locationStorage[latitudeKey] = loc.latitude
            locationStorage[longitudeKey] = loc.longitude
        }
    }

    suspend fun getGeoLocation(): LatLng {
        return with(context.dataStore.data.first()) {
            LatLng(this[latitudeKey].orZero(), this[longitudeKey].orZero())
        }
    }

    companion object {
        private const val LAT_KEY = "latitude_key"
        private const val LONG_KEY = "longitude_key"
    }
}
