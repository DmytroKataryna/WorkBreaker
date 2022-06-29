package kataryna.app.work.breaker.data.repository

import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.data.local.LocalLocationStorage
import kataryna.app.work.breaker.domain.GeoTrackingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeoTrackingRepositoryImpl @Inject constructor(
    private val local: LocalLocationStorage
) : GeoTrackingRepository {

    override suspend fun getGeoLocation(): LatLng {
        return local.getGeoLocation()
    }

    override suspend fun saveLocation(loc: LatLng) {
        local.saveLocation(loc)
    }
}