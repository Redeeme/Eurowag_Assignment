package eurowag.assignment.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.di.DefaultDispatcher
import eurowag.assignment.di.IoDispatcher
import eurowag.assignment.domain.managers.LocationManager
import eurowag.assignment.domain.mappers.LocationPointMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val locationManager: LocationManager,
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher,
    @DefaultDispatcher private val dispatcherDefault: CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    init {
        Log.d("eventtt","init")
        viewModelScope.launch(dispatcherIO) {
            repository.getAllFlow().collect { entities ->

                val newLocations = LocationPointMapper.asDomainEntity(entities)
                _state.update { currentState ->
                    currentState.copy(
                        locations = LocationPointMapper.asState(newLocations)
                    )

                }
            }
        }
        _state.update { currentState ->
            currentState.copy(
                isTracking = locationManager.isTracking
            )
        }
        codewars()
    }

    fun startTracking() {
        try {
            locationManager.startLocationUpdates()
            _state.update { currentState ->
                currentState.copy(
                    isTracking = true
                )
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error starting tracking", e)
        }
    }

    fun stopTracking() {
        locationManager.stopLocationUpdates()
        _state.update { currentState ->
            currentState.copy(
                isTracking = false
            )
        }
    }

    fun zoomToShowPins() {
        if (state.value.locations.isNotEmpty()) {
            viewModelScope.launch(dispatcherDefault) {

                val builder = LatLngBounds.Builder()
                state.value.locations.forEach { location ->
                    builder.include(LatLng(location.latitude, location.longitude))
                }
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

    fun checkPermissions(context: Context): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    //codewars
    fun codewars(){

    }

    fun twoSum(numbers: IntArray, target: Int): Pair<Int, Int> {
        val map = HashMap<Int, Int>()
        numbers.forEachIndexed { index, num ->
            map[target - num]?.let { return Pair(it, index) }
            map[num] = index
        }
        throw IllegalArgumentException("No solution found")
    }
}
