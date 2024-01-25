package com.pseteamtwo.allways.trip.tracking

import android.location.Location
import com.pseteamtwo.allways.trip.Mode

interface TrackingAlgorithm {
    fun observeTrackingData()

    fun predictLocation(location: Location): String

    fun predictTrip(locations: List<Location>)

    fun predictStage(tripLocations: List<Location>)

    fun predictMode(stageLocations: List<Location>): Mode
}