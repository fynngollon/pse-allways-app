package com.pseteamtwo.allways.data.trip.tracking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackingNotification : Application() {

    override fun onCreate() {
        super.onCreate()

        val channelLocationTracking = NotificationChannel(
            LOCATION_TRACKING_CHANNEL_ID,
            LOCATION_TRACKING_NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        /*
        val channelActivityRecognition = NotificationChannel(
            ACTIVITY_RECOGNITION_CHANNEL_ID,
            ACTIVITY_RECOGNITION_NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
         */

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channelLocationTracking)
        //notificationManager.createNotificationChannel(channelActivityRecognition)

    }
}