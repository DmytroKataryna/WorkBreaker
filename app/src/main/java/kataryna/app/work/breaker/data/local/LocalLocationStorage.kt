package kataryna.app.work.breaker.data.local

import com.google.android.gms.maps.model.LatLng

interface LocalLocationStorage {
    suspend fun getGeoLocation(): LatLng?
    suspend fun saveLocation(loc: LatLng)
    suspend fun clearLocation()
    suspend fun isLocationValid(): Boolean
}
