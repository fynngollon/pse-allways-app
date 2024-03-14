package com.pseteamtwo.allways.data.trip.tracking

import android.location.Location
import kotlin.math.abs

fun calculateSpeedBetweenLocations(startLocation: Location, endLocation: Location): Float {
    val elapsedTime = abs(endLocation.time - startLocation.time) / 1000.0F
    return startLocation.distanceTo(endLocation) / elapsedTime
}