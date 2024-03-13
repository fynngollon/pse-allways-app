package com.pseteamtwo.allways.data.trip.tracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.hasTrackingPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (!hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)) {
            return false
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (!hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            return false
        }
    }

    listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
    ).forEach {
        if (!hasPermission(it))  {
            return false
        }
    }

    return true
}

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}