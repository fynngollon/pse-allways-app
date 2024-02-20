package com.pseteamtwo.allways.trip.tracking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.pseteamtwo.allways.settings.AppPreferences
import javax.inject.Inject

// From Gemini:
// Error Handling: While you include permission checks, consider adding more robust error handling.
// For example, handle scenarios where GPS is unavailable or location updates fail consistently.
// Consider Battery Optimization: Explore strategies like geofencing or foreground notifications to
// minimize battery impact while maintaining functionality, especially if tracking intervals are long.
class GooglePlayServicesLocationProvider(
    private var locationResultHandler: DefaultLocationResultHandler
) : LocationProvider() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private fun createRequest(): LocationRequest =
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            AppPreferences(this).trackingRegularity.regularity
        ).build()

    fun updateRequest() {
        locationRequest = createRequest()
        stopLocationUpdates()
        startLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResultHandler.handleLocationResult(locationResult)
            }
        }
    }



    override fun startLocationUpdates() {
        if (AppPreferences(this).isTrackingEnabled) {
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}