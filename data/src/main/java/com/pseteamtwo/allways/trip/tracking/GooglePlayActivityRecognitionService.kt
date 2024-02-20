package com.pseteamtwo.allways.trip.tracking

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity

class GooglePlayActivityRecognitionService : ActivityRecognitionService() {

    // Checks if the user Android version is Android 10 (29+) or later
    private val runningAndroidTenOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    // List of activity transitions to be monitored
    private val activityTransitions: List<ActivityTransition> by lazy {
        listOf(
            getUserActivity(
                DetectedActivity.STILL, ActivityTransition.ACTIVITY_TRANSITION_ENTER
            ),
            getUserActivity(
                DetectedActivity.STILL, ActivityTransition.ACTIVITY_TRANSITION_EXIT
            ),
            getUserActivity(
                DetectedActivity.WALKING, ActivityTransition.ACTIVITY_TRANSITION_ENTER
            ),
            getUserActivity(
                DetectedActivity.WALKING, ActivityTransition.ACTIVITY_TRANSITION_EXIT
            ),
            getUserActivity(
                DetectedActivity.RUNNING, ActivityTransition.ACTIVITY_TRANSITION_ENTER
            ),
            getUserActivity(
                DetectedActivity.RUNNING, ActivityTransition.ACTIVITY_TRANSITION_EXIT
            ),
            getUserActivity(
                DetectedActivity.ON_BICYCLE, ActivityTransition.ACTIVITY_TRANSITION_ENTER
            ),
            getUserActivity(
                DetectedActivity.ON_BICYCLE, ActivityTransition.ACTIVITY_TRANSITION_EXIT
            ),
            getUserActivity(
                DetectedActivity.IN_VEHICLE, ActivityTransition.ACTIVITY_TRANSITION_ENTER
            ),
            getUserActivity(
                DetectedActivity.IN_VEHICLE, ActivityTransition.ACTIVITY_TRANSITION_EXIT
            )
        )
    }

    private val request = ActivityTransitionRequest(activityTransitions)

    private val transitionReceiver = TransitionsReceiver()

    private val activityClient = ActivityRecognition.getClient(this)

    private val customIntentUserAction = "ALLWAYS-USER-ACTIVITY-DETECTION-INTENT-ACTION"

    private val pendingIntent by lazy {
        PendingIntent.getService(
            this,
            0,
            Intent(customIntentUserAction),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getUserActivity(detectedActivity: Int, transitionType: Int): ActivityTransition {
        return ActivityTransition.Builder().setActivityType(detectedActivity)
            .setActivityTransition(transitionType).build()
    }

    override fun onBind(intent: Intent?): IBinder? = null


}