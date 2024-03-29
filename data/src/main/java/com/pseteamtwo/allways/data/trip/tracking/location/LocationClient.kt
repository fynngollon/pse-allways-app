package com.pseteamtwo.allways.data.trip.tracking.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    /**
     * Provides a flow to the caller of the latest user locations.
     * The caller can set an interval that determines how often he receives new location updates.
     *
     * @param interval determines how often the caller receives updates.
     * @return a hot flow of [Location].
     */
    fun getLocationUpdates(interval: Long): Flow<Location>
}