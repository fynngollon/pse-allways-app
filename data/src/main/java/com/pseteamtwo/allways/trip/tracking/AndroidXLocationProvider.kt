package com.pseteamtwo.allways.trip.tracking

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent

class AndroidXLocationProvider(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val locationResultHandler: LocationResultHandler
) : LocationProvider {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        FusedLocationProviderClient(context)
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
}