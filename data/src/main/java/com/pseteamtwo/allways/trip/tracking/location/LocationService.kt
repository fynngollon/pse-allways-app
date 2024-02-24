package com.pseteamtwo.allways.trip.tracking.location

import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.settings.AppPreferences
import com.pseteamtwo.allways.trip.repository.DefaultTripAndStageRepository
import com.pseteamtwo.allways.trip.tracking.LOCATION_TRACKING_CHANNEL_ID
import com.pseteamtwo.allways.trip.tracking.LOCATION_TRACKING_NOTIFICATION_ID
import com.pseteamtwo.allways.trip.tracking.LOCATION_TRACKING_NOTIFICATION_TEXT
import com.pseteamtwo.allways.trip.tracking.LOCATION_TRACKING_NOTIFICATION_TITLE
import com.pseteamtwo.allways.trip.tracking.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

//@AndroidEntryPoint
class LocationService @Inject constructor(
    private val tripAndStageRepository: DefaultTripAndStageRepository
) : TrackingService() {

    private lateinit var locationClient: LocationClient

    override fun onCreate() {
        super.onCreate()

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    @SuppressLint("NotificationPermission")
    override fun start() {
        val notification = buildNotification()

        if (!AppPreferences(this).isTrackingEnabled) {
            stop()
            return
        }

        locationClient
            .getLocationUpdates(AppPreferences(this).trackingRegularity.regularity)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                tripAndStageRepository.createGpsPoint(location)
            }
            .launchIn(serviceScope)

        startForeground(LOCATION_TRACKING_NOTIFICATION_ID, notification.build())
    }

    override fun buildNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, LOCATION_TRACKING_CHANNEL_ID)
            .setContentTitle(LOCATION_TRACKING_NOTIFICATION_TITLE)
            .setContentText(
                LOCATION_TRACKING_NOTIFICATION_TEXT
            )
            .setSmallIcon(R.mipmap.allways_app_icon)
            .setOngoing(true)
    }

}