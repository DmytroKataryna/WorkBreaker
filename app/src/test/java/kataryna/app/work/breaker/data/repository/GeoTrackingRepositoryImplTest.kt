package kataryna.app.work.breaker.data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.data.local.LocalLocationStorage
import kataryna.app.work.breaker.domain.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class GeoTrackingRepositoryImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val local: LocalLocationStorage = mock()
    private val repo = GeoTrackingRepositoryImpl(local)

    @Test
    fun checkGeofencingStatus() {
        runBlocking {
            repo.checkGeofencingStatus()
            verify(local).isLocationValid()
        }
    }

    @Test
    fun clearLocation() {
        runBlocking {
            repo.clearLocation()
            verify(local).clearLocation()
        }
    }

    @Test
    fun saveLocation() {
        runBlocking {
            val loc = LatLng(7.8, 5.9)
            repo.saveLocation(loc)
            verify(local).saveLocation(eq(loc))
        }
    }

    @Test
    fun getGeoLocation() {
        runBlocking {
            val result = repo.getGeoLocation().toList()
            verify(local).getGeoLocation()
            assertEquals(2, result.size)
            assertTrue(result[0] is Resource.Loading)
            assertTrue(result[1] is Resource.Success)
        }
    }
}