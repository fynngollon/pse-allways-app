package com.pseteamtwo.allways.trip.tracking

import android.location.Location
import com.pseteamtwo.allways.trip.Mode

class DefaultTrackingAlgorithm : TrackingAlgorithm {
    override fun observeTrackingData() {
        TODO("Not yet implemented")
    }

    override fun predictLocation(location: Location): String {
        TODO("Not yet implemented")
    }

    override fun predictTrip(locations: List<Location>) {
        TODO("Not yet implemented")
    }

    override fun predictStage(tripLocations: List<Location>) {
        TODO("Not yet implemented")
    }

    override fun predictMode(stageLocations: List<Location>): Mode {
        TODO("Not yet implemented")
    }
}