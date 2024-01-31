package com.pseteamtwo.allways.trip.source.network

import kotlinx.coroutines.sync.Mutex

class DefaultTripNetworkDataSource : TripNetworkDataSource {
    private val accessMutex = Mutex()

    override suspend fun loadTrips(): List<NetworkTrip> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTrips(trips: List<NetworkTrip>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(id: String): Int {
        TODO("Not yet implemented")
    }
}