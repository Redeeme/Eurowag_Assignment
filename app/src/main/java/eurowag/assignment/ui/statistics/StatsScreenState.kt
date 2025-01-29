package eurowag.assignment.ui.statistics

import com.github.mikephil.charting.data.Entry
import eurowag.assignment.database.entities.LocationPointEntity

data class StatsScreenState(
    val locations: List<LocationPointEntity> = emptyList(),
    val totalDistance: Double = 0.0,
    val averageSpeed: Double = 0.0,
    val minSpeed: Double = 0.0,
    val maxSpeed: Double = 0.0,
    val avgAlt: Double = 0.0,
    val entries: MutableList<Entry> = mutableListOf(),
)