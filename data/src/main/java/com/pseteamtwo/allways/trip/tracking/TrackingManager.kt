package com.pseteamtwo.allways.trip.tracking

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import javax.inject.Inject

class TrackingManager(
    /**
     * Context of an activity.
     */
    private val context: Context
) {

    /**
     * Provides user location updates for tracking.
     */
    private val locationProvider = GooglePlayServicesLocationProvider(locationResultHandler)

    /**
     * Handles the tracking result of the location provider
     */
    @Inject private lateinit var locationResultHandler: DefaultLocationResultHandler

    /**
     * Sets up user activity recognition.
     */
    private val activityRecognitionService = GooglePlayActivityRecognitionService()

    /**
     * Receiver of the user activity transitions.
     */
    private val transitionsReceiver = TransitionsReceiver()

    /**
     * Combines the user location tracking and the user activity recognition to predict trips and
     * stages.
     */
    private val trackingAlgorithm = DefaultTrackingAlgorithm()


    fun startTracking() {
        val intent = Intent(context, GooglePlayServicesLocationProvider::class.java)
        context.startService(intent)


        // TODO: Register the BroadcastReceiver to listen for activity transitions.
        // registerReceiver(mTransitionsReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))

    }
}