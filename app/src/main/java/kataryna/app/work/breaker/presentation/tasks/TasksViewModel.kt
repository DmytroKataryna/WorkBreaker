package kataryna.app.work.breaker.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kataryna.app.work.breaker.domain.Resource
import kataryna.app.work.breaker.domain.dispatchers.AppDispatchers
import kataryna.app.work.breaker.domain.repo.GeoTrackingRepository
import kataryna.app.work.breaker.domain.repo.UnsplashPhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dispatchers: AppDispatchers,
    private val repository: UnsplashPhotoRepository,
    private val geoTrackingRepository: GeoTrackingRepository
) : ViewModel() {

    var state = MutableStateFlow(TasksUiState(isLoading = true))

    fun fetchBackgroundPhoto() {
        viewModelScope.launch(dispatchers.default) {
            repository.getBackgroundPhoto().collect {
                state.emit(
                    when (it) {
                        is Resource.Loading -> state.value.copy(isLoading = true, exception = null)
                        is Resource.Error -> state.value.copy(
                            exception = it.message.orEmpty(),
                            isLoading = false
                        )
                        is Resource.Success -> state.value.copy(
                            bgImageUrl = it.data?.url.orEmpty(),
                            isLoading = false,
                            exception = null
                        )
                    }
                )
            }
        }
    }

    fun saveUserInput(text: String) {
        viewModelScope.launch(dispatchers.default) {
            repository.saveUserTasks(text)
        }
    }

    fun fetchUserTasks() {
        viewModelScope.launch(dispatchers.default) {
            repository.fetchUserTasks().collect {
                state.emit(
                    when (it) {
                        is Resource.Error -> state.value.copy(
                            exception = it.message.orEmpty(),
                            isLoading = false
                        )
                        is Resource.Loading -> state.value.copy(isLoading = true, exception = null)
                        is Resource.Success -> state.value.copy(
                            userTasks = it.data.orEmpty(),
                            isLoading = false,
                            exception = null
                        )
                    }
                )
            }
        }
    }

    fun checkGeofencingStatus() {
        viewModelScope.launch(dispatchers.default) {
            geoTrackingRepository.checkGeofencingStatus().let {
                state.emit(
                    state.value.copy(
                        geofencingStatus = it,
                        isLoading = false,
                        exception = null
                    )
                )
            }
        }
    }

    data class TasksUiState(
        val geofencingStatus: Boolean = false,
        val userTasks: String = "",
        val bgImageUrl: String? = null,
        val exception: String? = null,
        val isLoading: Boolean = false
    )
}
