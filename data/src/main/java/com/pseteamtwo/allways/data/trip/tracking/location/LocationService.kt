package com.pseteamtwo.allways.data.trip.tracking.location

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.data.settings.AppPreferences
import com.pseteamtwo.allways.data.trip.repository.TripAndStageRepository
import com.pseteamtwo.allways.data.trip.tracking.DefaultTrackingAlgorithm
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_CHANNEL_ID
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_NOTIFICATION_ID
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_NOTIFICATION_TEXT
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_NOTIFICATION_TITLE
import com.pseteamtwo.allways.data.trip.tracking.TrackingAlgorithmManager
import com.pseteamtwo.allways.data.trip.tracking.TrackingService
import com.pseteamtwo.allways.data.trip.tracking.calculateSpeedBetweenLocations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * NOT INTEGRATED YET. Needs to be granted permission and MainActivity.startTracking() (in
 * MainActivityExt.kt in the ui module) needs to be called to start the tracking.
 *
 * A foreground service that runs continuously while tracking is activated. The service start the
 * tracking and builds a notification that notifies the user of the fact that he is being tracked.
 * Since this is a persistent notification, it guaranties that the service is seen as a foreground
 * and not a background service. Using a background service would need more user permissions and
 * thus would be granted less often.
 * The service holds of instance to [LocationClient]. This [LocationClient] provides the actual
 * location updates. The LocationService listens to those updates and saves them into the database.
 */

@AndroidEntryPoint
class LocationService : TrackingService() {

    @Inject lateinit var tripAndStageRepository: TripAndStageRepository
    @Inject lateinit var trackingAlgorithmManager: TrackingAlgorithmManager

    private lateinit var locationClient: LocationClient
    private var lastLocation: Location? = null

    /**
     * Calls the parent class and initializes the [LocationClient].
     */
    override fun onCreate() {
        super.onCreate()

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    /**
     * If tracking is enabled, it builds a notification and listens to location tracking updates.
     * When it receives an update it stores the new location in the trip database as a
     * [com.pseteamtwo.allways.data.trip.source.local.LocalGpsPoint] and keeps the service running.
     */
    @SuppressLint("NotificationPermission")
    override fun start() {
        if (!AppPreferences(this).isTrackingEnabled) {
            stop()
            return
        }

        val notification = buildNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(AppPreferences(this).trackingRegularity.regularity)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                if (lastLocation != null) {
                    val speed = calculateSpeedBetweenLocations(lastLocation!!, location)
                    location.speed = speed
                }
                lastLocation = location

                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)\nSpeed: ${location.speed}"
                )
                notificationManager.notify(1, updatedNotification.build())
                Log.d("PSE_TRACKING", "-------------------------------------------------")
                Log.d("PSE_TRACKING", "Location: ($lat, $long)")
                Log.d("PSE_TRACKING", "Speed: ${location.speed}")
                tripAndStageRepository.createGpsPoint(location)
                trackingAlgorithmManager.requestAlgorithm(location)
            }
            .launchIn(serviceScope)


        startForeground(LOCATION_TRACKING_NOTIFICATION_ID, notification.build())
    }

    /**
     * Builds a notification that is displayed while the location is active. This ensures, that the
     * services is recognized as a foreground service.
     */
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