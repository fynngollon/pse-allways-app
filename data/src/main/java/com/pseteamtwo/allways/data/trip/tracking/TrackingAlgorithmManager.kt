package com.pseteamtwo.allways.data.trip.tracking

import android.location.Location
import com.pseteamtwo.allways.data.trip.tracking.algorithm.DefaultTrackingAlgorithm
import javax.inject.Inject

class TrackingAlgorithmManager @Inject constructor(
    private val trackingAlgorithm: DefaultTrackingAlgorithm
) {

    private var timestampLastMotionless = 0L

    fun requestAlgorithm(lastLocation: Location) {
        if (lastLocation.speed < DefaultTrackingAlgorithm.STILL_MOTION_THRESHOLD) {
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

    fun startAlgorithmManually() {
        trackingAlgorithm.observeTrackingData()
    }


}