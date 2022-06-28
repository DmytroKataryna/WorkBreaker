package kataryna.app.work.breaker.presentation.tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kataryna.app.work.breaker.domain.UnsplashPhotoRepository
import kataryna.app.work.breaker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: UnsplashPhotoRepository
) : ViewModel() {

    var state = MutableLiveData<TasksUiState>()

    fun fetchBackgroundPhoto() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.getBackgroundPhoto().collect {
                    when (it) {
                        is Resource.Loading -> state.postValue(TasksUiState.Loading)
                        is Resource.Error -> state.postValue(TasksUiState.Error(it.message.orEmpty()))
                        is Resource.Success -> state.postValue(TasksUiState.ScreenBackground(it.data?.url.orEmpty()))
                    }
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
            }.let {
                state.postValue(TasksUiState.UserTasks(it.orEmpty()))
            }
        }
    }

    sealed class TasksUiState {
        data class ScreenBackground(val bgImageUrl: String) : TasksUiState()
        data class UserTasks(val userTasks: String) : TasksUiState()
        data class Error(val exception: String) : TasksUiState()
        object Loading : TasksUiState()
    }
}