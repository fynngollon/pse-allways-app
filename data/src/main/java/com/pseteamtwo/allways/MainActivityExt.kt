package com.pseteamtwo.allways

import android.content.Intent
import com.pseteamtwo.allways.data.settings.AppPreferences
import com.pseteamtwo.allways.data.trip.tracking.ACTION_START
import com.pseteamtwo.allways.data.trip.tracking.ACTION_STOP
import com.pseteamtwo.allways.data.trip.tracking.location.LocationService

fun MainActivity.startTracking() {
    Intent(applicationContext, LocationService::class.java).apply {
        action = ACTION_START
        startService(this)
    }

    AppPreferences(this).isTrackingEnabled = true
}

fun MainActivity.stopTracking() {
    Intent(applicationContext, LocationService::class.java).apply {
        action = ACTION_STOP
        startService(this)
    }

    AppPreferences(this).isTrackingEnabled = false
}

