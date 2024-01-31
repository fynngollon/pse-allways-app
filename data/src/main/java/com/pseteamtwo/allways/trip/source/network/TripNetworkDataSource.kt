package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface TripNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadTrips(): List<NetworkTrip>

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveTrips(trips: List<NetworkTrip>)

    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteTrip(id: String): Int
}