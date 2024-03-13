package com.pseteamtwo.allways.data.trip.tracking

import android.location.Location
import kotlin.math.roundToInt

fun calculateDistance(locations: List<Location>): Int {
    var totalDistance = 0f
    var lastLocationTaken = locations.first()

    locations.forEach { location ->
        if (location.time > lastLocationTaken.time + INTERVAL_BETWEEN_LOCATIONS_TO_TAKE) {
            totalDistance += location.distanceTo(lastLocationTaken)
            lastLocationTaken = location
        }
    }

    totalDistance += locations.last().distanceTo(lastLocationTaken)

    return totalDistance.roundToInt()
}

const val INTERVAL_BETWEEN_LOCATIONS_TO_TAKE = 30000 // in milliseconds