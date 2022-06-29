package kataryna.app.work.breaker.domain

import com.google.android.gms.maps.model.LatLng

interface GeoTrackingRepository {

    suspend fun getGeoLocation(): LatLng

    suspend fun saveLocation(loc: LatLng)
}