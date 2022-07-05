package kataryna.app.work.breaker.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kataryna.app.work.breaker.domain.GeoTrackingRepository
import kataryna.app.work.breaker.domain.UnsplashPhotoRepository
import kataryna.app.work.breaker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: UnsplashPhotoRepository,
    private val geoTrackingRepository: GeoTrackingRepository
) : ViewModel() {

    var state = MutableStateFlow(TasksUiState(isLoading = true))

    fun fetchBackgroundPhoto() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.getBackgroundPhoto().collect {
                    val result = when (it) {
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
                    state.emit(result)
                }
            }
        }
    }

    fun saveUserInput(text: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveUserTasks(text)
            }
        }
    }

    fun fetchUserTasks() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.fetchUserTasks()
            }.collect {
                val result = when (it) {
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
                state.emit(result)
            }
        }
    }

    fun checkGeofencingStatus() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                geoTrackingRepository.checkGeofencingStatus()
            }.let {
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