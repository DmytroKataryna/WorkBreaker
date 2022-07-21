package kataryna.app.work.breaker.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kataryna.app.work.breaker.domain.Resource
import kataryna.app.work.breaker.domain.dispatchers.AppDispatchers
import kataryna.app.work.breaker.domain.repo.GeoTrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val dispatchers: AppDispatchers,
    private val repository: GeoTrackingRepository
) : ViewModel() {

    var state = MutableStateFlow(MapUiState())

    fun fetchGeoLocation() {
        viewModelScope.launch(dispatchers.default) {
            repository.getGeoLocation().collect {
                state.emit(
                    when (it) {
                        is Resource.Error -> state.value.copy(
                            exception = it.message,
                            isLoading = false
                        )
                        is Resource.Loading -> state.value.copy(isLoading = true, exception = null)
                        is Resource.Success -> state.value.copy(
                            location = it.data,
                            isLoading = false,
                            exception = null
                        )
                    }
                )
            }
        }
    }

    fun saveUserPickedLocation(loc: LatLng) {
        viewModelScope.launch(dispatchers.default) {
            repository.saveLocation(loc)
        }
    }

    fun clearUserPickedLocation() {
        viewModelScope.launch(dispatchers.default) {
            repository.clearLocation()
        }
    }

    data class MapUiState(
        val location: LatLng? = null,
        val exception: String? = null,
        val isLoading: Boolean = false
    )
}
