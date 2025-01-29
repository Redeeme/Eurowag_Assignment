package eurowag.assignment.domain.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import eurowag.assignment.database.entities.LocationPointEntity
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.utils.MySharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepo: LocationRepository
) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private lateinit var locationCallback: LocationCallback

    private lateinit var locationRequest: LocationRequest

    private val scope = CoroutineScope(Dispatchers.IO)

    var isTracking: Boolean = false

    private fun getLocationUpdates() {
        val prefs = MySharedPreferences(context)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateDistanceMeters(170f)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(5000)
                .setMinUpdateDistanceMeters(0f)
                .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    locationResult.lastLocation?.let { location ->
                        scope.launch {
                            Log.d("eventtt","locationCallback")
                            locationRepo.insert(
                                LocationPointEntity(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    accuracy = location.accuracy,
                                    provider = location.provider ?: "",
                                    time = location.time,
                                    altitude = location.altitude
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        getLocationUpdates()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        isTracking = true
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTracking = false
    }
}

