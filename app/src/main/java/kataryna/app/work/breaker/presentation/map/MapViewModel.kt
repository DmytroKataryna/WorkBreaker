package kataryna.app.work.breaker.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kataryna.app.work.breaker.domain.GeoTrackingRepository
import kataryna.app.work.breaker.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: GeoTrackingRepository
) : ViewModel() {

    var state = MutableStateFlow(MapUiState())

    fun fetchGeoLocation() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.getGeoLocation()
            }.collect {
                val result = when (it) {
                    is Resource.Error -> state.value.copy(exception = it.message, isLoading = false)
                    is Resource.Loading -> state.value.copy(isLoading = true, exception = null)
                    is Resource.Success -> state.value.copy(
                        location = it.data,
                        isLoading = false,
                        exception = null
                    )
                }
                state.emit(result)
            }
        }
    }

    fun saveUserPickedLocation(loc: LatLng) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.saveLocation(loc)
            }
        }
    }

    fun clearUserPickedLocation() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                repository.clearLocation()
            }
        }
    }

    data class MapUiState(
        val location: LatLng? = null,
        val exception: String? = null,
        val isLoading: Boolean = false
    )
}