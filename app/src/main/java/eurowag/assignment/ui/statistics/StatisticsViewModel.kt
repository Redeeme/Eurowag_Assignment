package eurowag.assignment.ui.statistics

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import eurowag.assignment.database.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: LocationRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(StatsScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    locations = repository.getAll()
                )
            }
            if (state.value.locations.isNotEmpty()) {
                loadStats()
            }
        }
    }

    private fun loadStats() {
        var i = 0
        var totalDistance = 0.0
        var totalTime = 0L
        var currentSpeed = 0.0
        var minSpeed = 0.0
        var maxSpeed = 0.0
        var totalAlt = 0.0
        var entries = mutableListOf<Entry>()

        while (i < state.value.locations.size - 1) {
            val startLoc = state.value.locations[i]
            val endLoc = state.value.locations[i + 1]

            val meters = FloatArray(1)
            Location.distanceBetween(
                startLoc.latitude,
                startLoc.longitude,
                endLoc.latitude,
                endLoc.longitude,
                meters
            )
            totalDistance += meters[0]
            totalTime += startLoc.time
            totalAlt += startLoc.altitude
            currentSpeed = (meters[0] * 1000.0) / (startLoc.time / 3600000)

            entries.add(Entry(i.toFloat(), currentSpeed.toFloat()))

            if (currentSpeed > maxSpeed) {
                maxSpeed = currentSpeed
            }

            if (currentSpeed < minSpeed) {
                minSpeed = currentSpeed
            }

            if (i == 0) {
                maxSpeed = currentSpeed
                minSpeed = currentSpeed
            }

            i++
        }

        totalTime += state.value.locations.last().time
        totalAlt += state.value.locations.last().altitude

        _state.update {
            it.copy(
                totalDistance = totalDistance,
                averageSpeed = (totalDistance * 1000) / (totalTime / 3600000),
                maxSpeed = maxSpeed,
                minSpeed = minSpeed,
                avgAlt = totalAlt / state.value.locations.size,
                entries = entries
            )
        }
    }
}