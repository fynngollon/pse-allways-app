package com.pseteamtwo.allways.data.trip.tracking.activityrecognition

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.pseteamtwo.allways.data.trip.tracking.ACTIVITY_RECOGNITION_PERMISSION_MISSING
import com.pseteamtwo.allways.data.trip.tracking.GPS_DISABLED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * NOT FUNCTIONAL YET
 */
class DefaultActivityTransitionClient(
    private val context: Context,
    private val client: ActivityRecognitionClient
) : ActivityTransitionClient {

    private lateinit var intent: Intent
    private lateinit var pendingIntent: PendingIntent

    /**
     * List of all activity transitions to be monitored.
     */
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

    @SuppressLint("MissingPermission")
    override fun getActivityTransitions(): Flow<ActivityTransitionResult> {
        return callbackFlow {

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                throw ActivityTransitionClient.ActivityTransitionException(GPS_DISABLED)
            }

            intent = Intent(context, BroadcastReceiver::class.java)
            //intent = Intent("TRANSITION_RECEIVER")
            pendingIntent = PendingIntent.getBroadcast(
                context, 1, intent, PendingIntent.FLAG_IMMUTABLE
            )

            val transitionReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val result = intent?.let { ActivityTransitionResult.extractResult(it) } ?: return
                    launch {
                        send(result)
                    }
                }
            }

            client.requestActivityTransitionUpdates(
                ActivityTransitionRequest(activityTransitions),
                pendingIntent
            )

            awaitClose {
                client.removeActivityTransitionUpdates(pendingIntent)
            }
        }
    }

    private fun getUserActivity(detectedActivity: Int, transitionType: Int): ActivityTransition {
        return ActivityTransition.Builder().setActivityType(detectedActivity)
            .setActivityTransition(transitionType).build()
    }

}