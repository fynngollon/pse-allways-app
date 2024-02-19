package com.pseteamtwo.allways.trip.tracking

import android.app.Service

abstract class LocationProvider: Service() {
    abstract fun startLocationUpdates()

    abstract fun stopLocationUpdates()
}