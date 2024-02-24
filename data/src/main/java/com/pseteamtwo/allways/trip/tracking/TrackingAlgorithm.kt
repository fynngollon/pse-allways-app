package com.pseteamtwo.allways.trip.tracking

import android.location.Location
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.LocalTrip

interface TrackingAlgorithm {
    fun observeTrackingData()
}