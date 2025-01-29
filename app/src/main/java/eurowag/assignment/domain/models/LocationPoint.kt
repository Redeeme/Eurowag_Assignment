package eurowag.assignment.domain.models

data class LocationPoint(
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val provider: String,
    val time: Long,
    val altitude: Double,
)