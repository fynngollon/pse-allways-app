package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface TripNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadTrips(pseudonym: String): List<NetworkTrip>

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveTrips(pseudonym: String, trips: List<NetworkTrip>)

    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteTrip(pseudonym: String, id: String): Int
}