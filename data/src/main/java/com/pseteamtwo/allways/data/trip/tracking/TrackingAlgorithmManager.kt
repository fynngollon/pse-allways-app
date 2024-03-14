package com.pseteamtwo.allways.data.trip.tracking

import android.location.Location
import android.util.Log
import javax.inject.Inject

class TrackingAlgorithmManager @Inject constructor(
    private val trackingAlgorithm: DefaultTrackingAlgorithm
) {

    private var timestampLastMotionless = 0L

    fun requestAlgorithm(lastLocation: Location) {
        Log.d("PSE_TRACKING", "TrackingAlgorithmManager: requesting to start algorithm")
        if (lastLocation.speed < DefaultTrackingAlgorithm.STILL_MOTION_THRESHOLD) {
            Log.d("PSE_TRACKING", "TrackingAlgorithmManager: Timestamp: $timestampLastMotionless")
            if (timestampLastMotionless == 0L) {
                timestampLastMotionless = lastLocation.time
            } else if (timestampLastMotionless + DefaultTrackingAlgorithm.MAX_MOTIONLESS_IN_TRIP < lastLocation.time) {
                trackingAlgorithm.observeTrackingData()
                timestampLastMotionless = 0L
            }
        } else {
            timestampLastMotionless = 0L
        }
    }


}