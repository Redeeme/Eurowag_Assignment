package eurowag.assignment

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.layouts.MainScreenState
import eurowag.assignment.managers.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().collect { locations ->
                _state.update { currentState ->
                    currentState.copy(
                        locations = locations
                    )
                }
            }
        }
    }



    fun startTracking(){
        try {
            locationManager.startLocationUpdates()
            _state.update {
                it.copy(isTracking = true)
            }
        } catch (e: Exception) {
            Log.e("ViewModel", "Error starting tracking", e)
        }
    }

    fun stopTracking(){
        locationManager.stopLocationUpdates()
        _state.update {
            it.copy(
                isTracking = false
            )
        }
    }

    fun zoomToShowPins(){
        if (state.value.locations.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            state.value.locations.forEach { location ->
                builder.include(LatLng(location.latitude, location.longitude))
            }
            viewModelScope.launch {
                _state.update { currentState ->
                    currentState.copy(
                        cameraPositionState = currentState.cameraPositionState.apply {
                            move(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
                        }
                    )
                }
            }
        }
    }

    fun selectLocation(id: Long) {
        _state.update { it.copy(selectedLocationId = id) }
    }
}