package eurowag.assignment.ui.uiStates

import androidx.compose.runtime.Stable

data class LocationPointState(
    val id: Long = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Float = 0f,
    val provider: String = "",
    val time: Long = 0L,
    val altitude: Double = 0.0,
)