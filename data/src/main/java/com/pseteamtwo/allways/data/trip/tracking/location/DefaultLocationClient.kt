package com.pseteamtwo.allways.data.trip.tracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * NOT INTEGRATED YET, BUT FULLY FUNCTIONAL. See [LocationClient] for more information.
 *
 * A Client that runs continuously while tracking is active. It receives the latest user locations
 * and provides them as a hot flow (callers keep getting updates) to other classes.
 */
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    /**
     * Returns a callback flow of the user's current location. This function can be called to
     * continuously get the last received tracking location updates of the user.
     * Firstly, it checks if the user has granted all necessary permissions to allow location
     * tracking and the device has internet connection and GPS enabled.
     * Then it creates a [LocationRequest], request updates and continuously listens to new
     * tracking location.
     * Upon received, it sends the locations into the flow for the listeners to collect.
     *
     * @throws [LocationClient.LocationException] if the user hasn't granted all necessary permissions for location
     * tracking
     * @param interval defines how often the users location should be requested. Note that the
     * frequency of locations returned by the [LocationRequest] from Google Play Services still
     * varies drastically depending on the battery percentage and the user's activity.
     * @return a callback flow (hot flow) of [Location] with the latest user locations.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGpsEnabled || isNetworkEnabled) {
                val request = LocationRequest.Builder(interval).build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let {
                            launch { send(it) }
                        }
                    }
                }

                client.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )

                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            }

        }
    }
}