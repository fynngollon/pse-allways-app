package com.pseteamtwo.allways.trip.tracking

import com.google.android.gms.location.LocationCallback

interface LocationProvider {
    //TODO("LocationCallback does not exist")
    fun startLocationUpdates()

    //TODO("LocationCallback does not exist")
    fun stopLocationUpdates()
}