package com.pseteamtwo.allways.trip.tracking

import com.google.android.gms.location.LocationCallback

interface LocationProvider {
    //TODO("LocationCallback does not exist")
    fun startLocationUpdates(locationCallback: LocationCallback)

    //TODO("LocationCallback does not exist")
    fun stopLocationUpdates(locationCallback: LocationCallback)
}