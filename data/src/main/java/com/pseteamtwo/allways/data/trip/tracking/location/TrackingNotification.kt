package com.pseteamtwo.allways.data.trip.tracking.location

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_CHANNEL_ID
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_NOTIFICATION_NAME
import dagger.hilt.android.HiltAndroidApp

/**
 * A notification that is active while the user location is being tracked.
 * With the help of this notification the [com.pseteamtwo.allways.data.trip.tracking.TrackingService]
 * is
 */
@HiltAndroidApp
class TrackingNotification : Application() {

    override fun onCreate() {
        super.onCreate()

        val channelLocationTracking = NotificationChannel(
            LOCATION_TRACKING_CHANNEL_ID,
            LOCATION_TRACKING_NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channelLocationTracking)
    }
}