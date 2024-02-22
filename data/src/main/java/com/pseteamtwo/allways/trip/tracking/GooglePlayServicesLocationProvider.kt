package com.pseteamtwo.allways.trip.tracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class GooglePlayServicesLocationProvider(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val locationResultHandler: LocationResultHandler
) : LocationProvider {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
            LocationServices.getFusedLocationProviderClient(context);
        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            handleLocationResult(locationResult)
        }
    }

    private fun handleLocationResult(locationResult: LocationResult) {
        locationResultHandler.handleLocationResult(locationResult)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startLocationUpdates() {
        // Start location updates using fusedLocationClient and pass the locationCallback
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10 * 1000 // 10 seconds
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopLocationUpdates() {
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}