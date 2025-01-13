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
        var totalTime = 0.0
        var minSpeed = 0.0
        var maxSpeed = 0.0
        var totalAlt = 0.0
        var entries = mutableListOf<Entry>()
        var avgSpeed = 0.0

        while (i < state.value.locations.size - 1) {

            val startLoc = state.value.locations[i]
            val endLoc = state.value.locations[i + 1]

            val startLocHours = startLoc.time.toDouble() / (1000.0 * 3600.0)
            val endLocHours = endLoc.time.toDouble() / (1000.0 * 3600.0)

            val diffHours = endLocHours - startLocHours

            var currentSpeed = 0.0
            var currentDistance = 0.0

            val meters = FloatArray(1)
            Location.distanceBetween(
                startLoc.latitude,
                startLoc.longitude,
                endLoc.latitude,
                endLoc.longitude,
                meters
            )
            if (meters[0] != 0f) {
                currentDistance = (meters[0] / 1000.0)
            }

            totalDistance += currentDistance
            totalTime += diffHours
            totalAlt += startLoc.altitude
            if ((currentDistance != 0.0)) {
                currentSpeed = currentDistance / diffHours
            }

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
        totalAlt += state.value.locations.last().altitude

        if (totalDistance != 0.0) {
            avgSpeed = totalDistance / totalTime
        }
        _state.update {
            it.copy(
                totalDistance = totalDistance,
                averageSpeed = avgSpeed,
                maxSpeed = maxSpeed,
                minSpeed = minSpeed,
                avgAlt = totalAlt / state.value.locations.size,
                entries = entries
            )
        }
    }
}