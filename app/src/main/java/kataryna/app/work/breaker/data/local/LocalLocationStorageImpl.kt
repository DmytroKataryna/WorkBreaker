package kataryna.app.work.breaker.data.local

import androidx.annotation.VisibleForTesting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first

class LocalLocationStorageImpl(
    private val dataStore: DataStore<Preferences>
) : LocalLocationStorage {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val latitudeKey = doublePreferencesKey(LAT_KEY)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val longitudeKey = doublePreferencesKey(LONG_KEY)

    override suspend fun saveLocation(loc: LatLng) {
        dataStore.edit { locationStorage ->
            locationStorage[latitudeKey] = loc.latitude
            locationStorage[longitudeKey] = loc.longitude
        }
    }

    override suspend fun getGeoLocation(): LatLng? {
        with(dataStore.data.first()) {
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
        dataStore.edit { locationStorage ->
            locationStorage[latitudeKey] = INVALID
            locationStorage[longitudeKey] = INVALID
        }
    }

    override suspend fun isLocationValid(): Boolean {
        val loc = getGeoLocation()
        return loc != null
    }

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val INVALID = -1.0

        private const val LAT_KEY = "latitude_key"
        private const val LONG_KEY = "longitude_key"
    }
}
