package eurowag.assignment.ui.main

import androidx.compose.foundation.text.input.TextFieldState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import eurowag.assignment.database.LocationPoint

data class MainScreenState(
    val locations: List<LocationPoint> = emptyList(),
    val isTracking: Boolean = false,
    val cameraPositionState: CameraPositionState = CameraPositionState(
        CameraPosition.fromLatLngZoom(
            LatLng(48.148598, 17.107748),
            12f
        )
    ),
)