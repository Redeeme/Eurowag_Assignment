package eurowag.assignment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import eurowag.assignment.database.LocationPoint
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.layouts.MainScreenState
import eurowag.assignment.managers.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val locationManager: LocationManager,
) : ViewModel() {
    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllFlow().collect { locations ->
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

    fun setInterval(){
        if (state.value.isTracking){
            stopTracking()
            startTracking()
        }
    }

    fun exportLocations(context: Context) {
        viewModelScope.launch {
            val locations = repository.getAll()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val types = Types.newParameterizedType(List::class.java, LocationPoint::class.java)
            val jsonAdapter = moshi.adapter<List<LocationPoint>>(types)
            val jsonString = jsonAdapter.toJson(locations)

            val file = File(context.cacheDir, "locations.json")
            file.writeText(jsonString)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Locations"))
        }
    }

    fun checkPermissions(context: Context): Boolean{
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }
}