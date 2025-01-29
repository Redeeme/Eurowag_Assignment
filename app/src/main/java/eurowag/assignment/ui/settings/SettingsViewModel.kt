package eurowag.assignment.ui.settings

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.di.DefaultDispatcher
import eurowag.assignment.di.IoDispatcher
import eurowag.assignment.domain.managers.LocationManager
import eurowag.assignment.domain.mappers.LocationPointMapper
import eurowag.assignment.domain.models.LocationPoint
import eurowag.assignment.utils.MySharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val locationManager: LocationManager,
    @IoDispatcher private val dispatcherIO: CoroutineDispatcher,
    @DefaultDispatcher private val dispatcherDefault: CoroutineDispatcher,
    val prefs: MySharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    fun setInterval(interval: Long) {
        prefs.setInterval(interval * 60000)
        if (locationManager.isTracking) {
            locationManager.stopLocationUpdates()
            locationManager.startLocationUpdates()
        }
    }

    fun exportLocations(context: Context) {
        viewModelScope.launch(dispatcherDefault) {
            val entities = repository.getAll()
            val locations = LocationPointMapper.asDomainEntity(entities)
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

    fun wipeData() {
        viewModelScope.launch(dispatcherIO) {
            repository.deleteAll()
        }
    }

    fun showDeleteDialog(show: Boolean){
        _state.update { currentState ->
            currentState.copy(
                showDeleteDialog = show
            )
        }
    }

    fun showIntervalDialog(show: Boolean){
        _state.update { currentState ->
            currentState.copy(
                showIntervalDialog = show
            )
        }
    }
}