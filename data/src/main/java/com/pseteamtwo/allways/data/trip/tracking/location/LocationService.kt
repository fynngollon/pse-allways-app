package com.pseteamtwo.allways.data.trip.tracking.location

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.data.settings.AppPreferences
import com.pseteamtwo.allways.data.trip.repository.TripAndStageRepository
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_CHANNEL_ID
import com.pseteamtwo.allways.data.trip.tracking.LOCATION_TRACKING_NOTIFICATION_ID
import com.pseteamtwo.allways.data.trip.tracking.TrackingAlgorithmManager
import com.pseteamtwo.allways.data.trip.tracking.hasLocationPermission
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
        if (!hasLocationPermission()) {
            AppPreferences(this).isTrackingEnabled = false
        }

        if (!AppPreferences(this).isTrackingEnabled) {
            stop()
            return
        }

        val notification = buildNotification()

        locationClient
            .getLocationUpdates(AppPreferences(this).trackingRegularity.regularity)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                if (lastLocation != null) {
                    val speed = calculateSpeedBetweenLocations(lastLocation!!, location)
                    location.speed = if (speed.isNaN() || speed.isInfinite()) 0.0F else speed
                }
                lastLocation = location

                Log.d("PSE_", AppPreferences(this).trackingRegularity.regularity.toString())

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
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText(
                getString(R.string.tracking_notification_text)
            )
            .setSmallIcon(R.mipmap.allways_app_icon)
            .setOngoing(true)
    }


    override fun startAlgorithm() {
        trackingAlgorithmManager.startAlgorithmManually()
    }
}