package eurowag.assignment.managers

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
        Log.d("asdfasdf", "ASDfasdf 1")
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(170f)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .setMinUpdateDistanceMeters(0f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d("asdfasdf", "ASDfasdf 2")
                if (locationResult.locations.isNotEmpty()) {
                    Log.d("asdfasdf", "ASDfasdf ${locationResult.lastLocation}")
                    locationResult.lastLocation?.let { location ->
                        scope.launch {
                            locationRepo.insert(
                                LocationPoint(
                                    latitude = location?.latitude ?: 0.0,
                                    longitude = location?.longitude ?: 0.0,
                                    accuracy = location?.accuracy ?: 0.0f,
                                    provider = location?.provider ?: "",
                                    time = location?.time ?: 0L,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun startLocationUpdates() {
        getLocationUpdates()
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

