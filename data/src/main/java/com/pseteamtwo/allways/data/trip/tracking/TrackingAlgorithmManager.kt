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
            Log.d("PSE_TRACKING", "TrackingAlgorithmManager: Speed under threshold")
            Log.d("PSE_TRACKING", "TrackingAlgorithmManager: Timestamp: $timestampLastMotionless")
            Log.d("PSE_TRACKING", "TrackingAlgorithmManager: Time elapsed: ${lastLocation.time - timestampLastMotionless}")
            if (timestampLastMotionless == 0L) {
                Log.d("PSE_TRACKING", "TrackingAlgorithmManager: Set timestamp")
                timestampLastMotionless = lastLocation.time
            } else if (timestampLastMotionless + DefaultTrackingAlgorithm.MAX_MOTIONLESS_IN_TRIP < lastLocation.time) {
                Log.d("PSE_TRACKING", "TrackingAlgorithmManager: 15min motionless elapsed. Starts algorithm now")
                trackingAlgorithm.observeTrackingData()
                timestampLastMotionless = 0L
            }
        } else {
            timestampLastMotionless = 0L
        }
    }


}