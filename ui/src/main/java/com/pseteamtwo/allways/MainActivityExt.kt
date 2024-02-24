package com.pseteamtwo.allways

import android.content.Intent
import androidx.compose.runtime.Composable
import com.pseteamtwo.allways.settings.AppPreferences
import com.pseteamtwo.allways.trip.tracking.ACTION_START
import com.pseteamtwo.allways.trip.tracking.ACTION_STOP
import com.pseteamtwo.allways.trip.tracking.location.LocationService

fun MainActivity.startTracking() {
    Intent(applicationContext, LocationService::class.java).apply {
        action = ACTION_START
        startService(this)
    }

    /*
    Intent(applicationContext, ActivityTransitionService::class.java).apply {
        action = ACTION_START
        startService(this)
    }
     */

    AppPreferences(this).isTrackingEnabled = true
}

fun MainActivity.stopTracking() {
    Intent(applicationContext, LocationService::class.java).apply {
        action = ACTION_STOP
        startService(this)
    }

    /*
    Intent(applicationContext, ActivityTransitionService::class.java).apply {
        action = ACTION_STOP
        startService(this)
    }
     */

    AppPreferences(this).isTrackingEnabled = false
}

@Composable
fun MainActivity.TrackingPermissionCheck() {
    androidx.core.app.ActivityCompat.requestPermissions(
        this,
        arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        ),
        0
    )

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        androidx.core.app.ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
            0)
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        androidx.core.app.ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            0)
    }
}