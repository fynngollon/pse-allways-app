package com.pseteamtwo.allways.trip.tracking.todo

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.pseteamtwo.allways.settings.AppPreferences
import com.pseteamtwo.allways.trip.tracking.ACTION_START
import com.pseteamtwo.allways.trip.tracking.ACTION_STOP
import com.pseteamtwo.allways.trip.tracking.location.LocationService

fun MainActivityTEMP.startTracking() { // TODO to MainActivity
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

fun MainActivityTEMP.stopTracking() { // TODO to MainActivity
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
fun MainActivityTEMP.TrackingPermissionCheck() { // TODO to MainActivity
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        ),
        0
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
            0)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            0)
    }
}