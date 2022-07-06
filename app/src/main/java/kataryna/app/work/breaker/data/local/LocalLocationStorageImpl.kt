package kataryna.app.work.breaker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location")

class LocalLocationStorageImpl(private val context: Context) : LocalLocationStorage {

    private val latitudeKey = doublePreferencesKey(LAT_KEY)
    private val longitudeKey = doublePreferencesKey(LONG_KEY)

    override suspend fun saveLocation(loc: LatLng) {
        context.dataStore.edit { locationStorage ->
            locationStorage[latitudeKey] = loc.latitude
            locationStorage[longitudeKey] = loc.longitude
        }
    }

    override suspend fun getGeoLocation(): LatLng? {
        with(context.dataStore.data.first()) {
            val lat = this[latitudeKey] ?: INVALID
            val long = this[longitudeKey] ?: INVALID
            return if (lat == INVALID && long == INVALID) {
                null
            } else {
                LatLng(lat, long)
            }
        }
    }

    override suspend fun clearLocation() {
        context.dataStore.edit { locationStorage ->
            locationStorage[latitudeKey] = INVALID
            locationStorage[longitudeKey] = INVALID
        }
    }

    override suspend fun isLocationValid(): Boolean {
        val loc = getGeoLocation()
        return loc != null
    }

    companion object {
        private const val LAT_KEY = "latitude_key"
        private const val LONG_KEY = "longitude_key"
        private const val INVALID = -1.0


    }
}
