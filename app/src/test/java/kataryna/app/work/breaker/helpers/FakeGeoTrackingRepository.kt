package kataryna.app.work.breaker.helpers

import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.domain.repo.GeoTrackingRepository
import kataryna.app.work.breaker.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGeoTrackingRepository : GeoTrackingRepository {
    override suspend fun getGeoLocation(): Flow<Resource<LatLng?>> {
        return flow { }
    }

    override suspend fun saveLocation(loc: LatLng) {
    }

    override suspend fun clearLocation() {
    }

    override suspend fun checkGeofencingStatus(): Boolean {
        return true
    }
}