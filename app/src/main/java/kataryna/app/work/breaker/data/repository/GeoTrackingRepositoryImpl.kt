package kataryna.app.work.breaker.data.repository

import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.data.local.LocalLocationStorage
import kataryna.app.work.breaker.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeoTrackingRepositoryImpl @Inject constructor(
    private val local: LocalLocationStorage
) : GeoTrackingRepository {

    override suspend fun getGeoLocation(): Flow<Resource<LatLng?>> {
        return flow {
            emit(Resource.Loading())
            emit(Resource.Success(data = local.getGeoLocation()))
        }
    }

    override suspend fun saveLocation(loc: LatLng) = local.saveLocation(loc)

    override suspend fun clearLocation() = local.clearLocation()

    override suspend fun checkGeofencingStatus() = local.isLocationValid()
}
