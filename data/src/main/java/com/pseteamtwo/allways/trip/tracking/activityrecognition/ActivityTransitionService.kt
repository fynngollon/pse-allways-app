package com.pseteamtwo.allways.trip.tracking.activityrecognition

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.ActivityRecognition
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.trip.tracking.ACTIVITY_RECOGNITION_CHANNEL_ID
import com.pseteamtwo.allways.trip.tracking.ACTIVITY_RECOGNITION_NOTIFICATION_ID
import com.pseteamtwo.allways.trip.tracking.ACTIVITY_RECOGNITION_NOTIFICATION_TEXT
import com.pseteamtwo.allways.trip.tracking.ACTIVITY_RECOGNITION_NOTIFICATION_TITLE
import com.pseteamtwo.allways.trip.tracking.TrackingService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


//@AndroidEntryPoint
class ActivityTransitionService : TrackingService() {

    private lateinit var activityTransitionsClient: ActivityTransitionClient

    override fun onCreate() {
        super.onCreate()

        activityTransitionsClient = DefaultActivityTransitionClient(
            applicationContext,
            ActivityRecognition.getClient(applicationContext)
        )
    }

    @SuppressLint("NotificationPermission")
    override fun start() {
        val notification = buildNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        activityTransitionsClient
            .getActivityTransitions()
            .catch { e -> e.printStackTrace() }
            .onEach { result ->
                val updateNotification = notification.setContentText(
                    ACTIVITY_RECOGNITION_NOTIFICATION_TEXT
                        .format(
                            getActivityType(result.transitionEvents[0].activityType) +
                                " - ${getTransitionType(result.transitionEvents[0].transitionType)}",
                            getActivityType(result.transitionEvents[1].activityType) +
                                    " - ${getTransitionType(result.transitionEvents[1].transitionType)}")

                )
                notificationManager.notify(ACTIVITY_RECOGNITION_NOTIFICATION_ID, updateNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(ACTIVITY_RECOGNITION_NOTIFICATION_ID, notification.build())
    }

    override fun buildNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, ACTIVITY_RECOGNITION_CHANNEL_ID)
            .setContentTitle(ACTIVITY_RECOGNITION_NOTIFICATION_TITLE)
            .setContentText(
                ACTIVITY_RECOGNITION_NOTIFICATION_TEXT
                    .format("null", "null")
            )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
    }

    private fun getActivityType(int: Int): String {
        return when (int) {
            0 -> "IN_VEHICLE"
            1 -> "ON_BICYCLE"
            2 -> "ON_FOOT"
            3 -> "STILL"
            4 -> "UNKNOWN"
            5 -> "TILTING"
            7 -> "WALKING"
            8 -> "RUNNING"
            else -> "UNKNOWN"
        }
    }

    private fun getTransitionType(int: Int): String {
        return when (int) {
            0 -> "STARTED"
            1 -> "STOPPED"
            else -> ""
        }
    }
}