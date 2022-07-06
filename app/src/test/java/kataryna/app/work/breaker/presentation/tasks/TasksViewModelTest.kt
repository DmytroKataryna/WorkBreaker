package kataryna.app.work.breaker.presentation.tasks

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import kataryna.app.work.breaker.TestDispatchers
import kataryna.app.work.breaker.domain.AppDispatchers
import kataryna.app.work.breaker.domain.GeoTrackingRepository
import kataryna.app.work.breaker.domain.UnsplashPhotoRepository
import kataryna.app.work.breaker.domain.model.UnsplashPhoto
import kataryna.app.work.breaker.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fetchBackgroundPhoto() {
        runBlocking {
            val photoRepo = spy(FakeUnsplashPhotoRepository())
            val geoRepo = spy(FakeGeoTrackingRepository())
            withViewModel(photoRepo = photoRepo, geoRepo = geoRepo) {
                fetchBackgroundPhoto()
                verify(photoRepo, times(1)).getBackgroundPhoto()
            }
        }
    }

    @Test
    fun saveUserInput() {
    }

    @Test
    fun fetchUserTasks() {
    }

    @Test
    fun checkGeofencingStatus() {

    }

    private suspend fun withViewModel(
        dispatchers: AppDispatchers = TestDispatchers(),
        photoRepo: UnsplashPhotoRepository = FakeUnsplashPhotoRepository(),
        geoRepo: GeoTrackingRepository = FakeGeoTrackingRepository(),
        action: suspend TasksViewModel.() -> Unit
    ) {
        TasksViewModel(dispatchers, photoRepo, geoRepo).apply {
            action.invoke(this)
        }
    }

    private class FakeUnsplashPhotoRepository() : UnsplashPhotoRepository {

        override suspend fun getBackgroundPhoto(): Flow<Resource<UnsplashPhoto>> {
            return flow { }
        }

        override suspend fun saveUserTasks(text: String) {

        }

        override suspend fun fetchUserTasks(): Flow<Resource<String?>> {
            return flow { }
        }

    }

    private class FakeGeoTrackingRepository : GeoTrackingRepository {
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
}