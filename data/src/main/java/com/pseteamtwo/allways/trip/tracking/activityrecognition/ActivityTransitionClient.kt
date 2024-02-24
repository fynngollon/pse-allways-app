package com.pseteamtwo.allways.trip.tracking.activityrecognition

import com.google.android.gms.location.ActivityTransitionResult
import kotlinx.coroutines.flow.Flow

/**
 * NOT FUNCTIONAL YET
 */
interface ActivityTransitionClient {
    fun getActivityTransitions(): Flow<ActivityTransitionResult>

    class ActivityTransitionException(message: String) : Exception()
}