package kataryna.app.work.breaker.data.repository

import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.data.Resource
import kotlinx.coroutines.flow.Flow

interface GeoTrackingRepository {

    suspend fun getGeoLocation(): Flow<Resource<LatLng?>>

    suspend fun saveLocation(loc: LatLng)

    suspend fun clearLocation()

    suspend fun checkGeofencingStatus(): Boolean
}
