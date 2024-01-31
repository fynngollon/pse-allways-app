package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface StageNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadStages(): List<NetworkStage>

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveStages(stages: List<NetworkStage>)

    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteStage(id: String): Int
}