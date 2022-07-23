package kataryna.app.work.breaker.presentation.features.tasks

import android.arch.core.executor.testing.InstantTaskExecutorRule
import kataryna.app.work.breaker.helpers.FakeGeoTrackingRepository
import kataryna.app.work.breaker.helpers.FakeUnsplashPhotoRepository
import kataryna.app.work.breaker.helpers.TestDispatchers
import kataryna.app.work.breaker.dispatchers.AppDispatchers
import kataryna.app.work.breaker.data.repository.GeoTrackingRepository
import kataryna.app.work.breaker.data.repository.UnsplashPhotoRepository
import kataryna.app.work.breaker.presentation.model.UnsplashPhoto
import kataryna.app.work.breaker.data.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fetchBackgroundPhotoSuccess() {
        val photoRepo = mock<UnsplashPhotoRepository> {
            onBlocking { getBackgroundPhoto() } doReturn flow {
                emit(Resource.Success(UnsplashPhoto("url")))
            }
        }
        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                fetchBackgroundPhoto()
                verify(photoRepo, times(1)).getBackgroundPhoto()
                with(this.state.value) {
                    assertEquals(bgImageUrl, "url")
                    assertEquals(isLoading, false)
                    assertEquals(exception, null)
                }
            }
        }
    }

    @Test
    fun fetchBackgroundPhotoLoading() {
        val photoRepo = mock<UnsplashPhotoRepository> {
            onBlocking { getBackgroundPhoto() } doReturn flow {
                emit(Resource.Loading())
            }
        }
        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                fetchBackgroundPhoto()
                verify(photoRepo, times(1)).getBackgroundPhoto()
                with(this.state.value) {
                    assertEquals(isLoading, true)
                    assertEquals(exception, null)
                }
            }
        }
    }

    @Test
    fun fetchBackgroundPhotoError() {
        val photoRepo = mock<UnsplashPhotoRepository> {
            onBlocking { getBackgroundPhoto() } doReturn flow {
                emit(Resource.Error(message = "Error Msg"))
            }
        }
        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                fetchBackgroundPhoto()
                verify(photoRepo, times(1)).getBackgroundPhoto()
                with(this.state.value) {
                    assertEquals(isLoading, false)
                    assertEquals(exception, "Error Msg")
                }
            }
        }
    }

    @Test
    fun saveUserInput() {
        val photoRepo = mock<UnsplashPhotoRepository>()

        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                saveUserInput("Input")
                verify(photoRepo, times(1)).saveUserTasks(any())
            }
        }
    }

    @Test
    fun fetchUserTasksSuccess() {
        val photoRepo = mock<UnsplashPhotoRepository> {
            onBlocking { fetchUserTasks() } doReturn flow {
                emit(Resource.Success("Tasks from user"))
            }
        }

        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                fetchUserTasks()
                verify(photoRepo, times(1)).fetchUserTasks()
                with(this.state.value) {
                    assertEquals(isLoading, false)
                    assertEquals(exception, null)
                    assertEquals(userTasks, "Tasks from user")
                }
            }
        }
    }

    @Test
    fun fetchUserTasksLoading() {
        val photoRepo = mock<UnsplashPhotoRepository> {
            onBlocking { fetchUserTasks() } doReturn flow {
                emit(Resource.Loading())
            }
        }

        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                fetchUserTasks()
                verify(photoRepo, times(1)).fetchUserTasks()
                with(this.state.value) {
                    assertEquals(isLoading, true)
                    assertEquals(exception, null)
                }
            }
        }
    }

    @Test
    fun fetchUserTasksError() {
        val photoRepo = mock<UnsplashPhotoRepository> {
            onBlocking { fetchUserTasks() } doReturn flow {
                emit(Resource.Error("Error msg"))
            }
        }

        runBlocking {
            withViewModel(photoRepo = photoRepo) {
                fetchUserTasks()
                verify(photoRepo, times(1)).fetchUserTasks()
                with(this.state.value) {
                    assertEquals(isLoading, false)
                    assertEquals(exception, "Error msg")
                }
            }
        }
    }

    @Test
    fun checkGeofencingStatus() {
        val geoRepo = mock<GeoTrackingRepository> {
            onBlocking { checkGeofencingStatus() } doReturn true
        }

        runBlocking {
            withViewModel(geoRepo = geoRepo) {
                checkGeofencingStatus()
                verify(geoRepo, times(1)).checkGeofencingStatus()
                assertEquals(true, this.state.value.geofencingStatus)
            }
        }
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
}
