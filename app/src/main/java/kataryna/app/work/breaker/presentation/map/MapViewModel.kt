package kataryna.app.work.breaker.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kataryna.app.work.breaker.domain.GeoTrackingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: GeoTrackingRepository
) : ViewModel() {

    var state = MutableLiveData<MapUiState>()

    fun fetchGeoLocation() {
        viewModelScope.launch {
            state.postValue(MapUiState.Loading)
            withContext(Dispatchers.Default) {
                val loc = repository.getGeoLocation()
                state.postValue(MapUiState.GeoLocation(loc))
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

    sealed class MapUiState {
        data class GeoLocation(val location: LatLng) : MapUiState()
        data class Error(val exception: String) : MapUiState()
        object Loading : MapUiState()
    }
}