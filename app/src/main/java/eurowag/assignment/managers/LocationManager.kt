package eurowag.assignment.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import eurowag.assignment.database.LocationPoint
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.database.MySharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun getLocationUpdates() {
        val prefs = MySharedPreferences(context)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, prefs.getInterval())
                .setWaitForAccurateLocation(false)
                .setMinUpdateDistanceMeters(170f)
                .setMinUpdateIntervalMillis(prefs.getInterval())
                .setMaxUpdateDelayMillis(prefs.getInterval() + 5000)
                .setMinUpdateDistanceMeters(0f)
                .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    locationResult.lastLocation?.let { location ->
                        scope.launch {
                            locationRepo.insert(
                                LocationPoint(
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
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

