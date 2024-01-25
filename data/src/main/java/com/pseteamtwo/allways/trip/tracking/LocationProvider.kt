package com.pseteamtwo.allways.trip.tracking

interface LocationProvider {
    //TODO("LocationCallback does not exist")
    fun startLocationUpdates(locationCallback: LocationCallback)

    //TODO("LocationCallback does not exist")
    fun stopLocationUpdates(locationCallback: LocationCallback)
}