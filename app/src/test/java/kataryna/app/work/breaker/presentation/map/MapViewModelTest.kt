package kataryna.app.work.breaker.presentation.map

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.helpers.FakeGeoTrackingRepository
import kataryna.app.work.breaker.helpers.TestDispatchers
import kataryna.app.work.breaker.domain.dispatchers.AppDispatchers
import kataryna.app.work.breaker.domain.repo.GeoTrackingRepository
import kataryna.app.work.breaker.domain.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MapViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fetchGeoLocationSuccess() {
        val loc = LatLng(5.5, 6.6)
        val geoRepo = mock<GeoTrackingRepository> {
            onBlocking { getGeoLocation() } doReturn flow { emit(Resource.Success(loc)) }
        }

        runBlocking {
            withViewModel(geoRepo = geoRepo) {
                fetchGeoLocation()
                verify(geoRepo, times(1)).getGeoLocation()
                with(this.state.value) {
                    Assert.assertEquals(location, loc)
                    Assert.assertEquals(isLoading, false)
                    Assert.assertEquals(exception, null)
                }
            }
        }
    }

    @Test
    fun fetchGeoLocationLoading() {
        val geoRepo = mock<GeoTrackingRepository> {
            onBlocking { getGeoLocation() } doReturn flow { emit(Resource.Loading()) }
        }

        runBlocking {
            withViewModel(geoRepo = geoRepo) {
                fetchGeoLocation()
                verify(geoRepo, times(1)).getGeoLocation()
                with(this.state.value) {
                    Assert.assertEquals(isLoading, true)
                    Assert.assertEquals(exception, null)
                }
            }
        }
    }

    @Test
    fun fetchGeoLocationError() {
        val geoRepo = mock<GeoTrackingRepository> {
            onBlocking { getGeoLocation() } doReturn flow { emit(Resource.Error("Error msg")) }
        }

        runBlocking {
            withViewModel(geoRepo = geoRepo) {
                fetchGeoLocation()
                verify(geoRepo, times(1)).getGeoLocation()
                with(this.state.value) {
                    Assert.assertEquals(isLoading, false)
                    Assert.assertEquals(exception, "Error msg")
                }
            }
        }
    }

    @Test
    fun saveUserPickedLocation() {
        val loc = LatLng(5.5, 6.6)
        val geoRepo = mock<GeoTrackingRepository>()

        runBlocking {
            withViewModel(geoRepo = geoRepo) {
                saveUserPickedLocation(loc)
                verify(geoRepo).saveLocation(eq(loc))
            }
        }
    }

    @Test
    fun clearUserPickedLocation() {
        val geoRepo = mock<GeoTrackingRepository>()

        runBlocking {
            withViewModel(geoRepo = geoRepo) {
                clearUserPickedLocation()
                verify(geoRepo).clearLocation()
            }
        }
    }

    private suspend fun withViewModel(
        dispatchers: AppDispatchers = TestDispatchers(),
        geoRepo: GeoTrackingRepository = FakeGeoTrackingRepository(),
        action: suspend MapViewModel.() -> Unit
    ) {
        MapViewModel(dispatchers, geoRepo).apply {
            action.invoke(this)
        }
    }
}
