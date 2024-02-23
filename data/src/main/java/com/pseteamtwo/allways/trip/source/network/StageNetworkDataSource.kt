package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface StageNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadStages(pseudonym: String): List<NetworkStage>

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveStages(pseudonym: String, stages: List<NetworkStage>)

    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteStage(pseudonym: String, id: String): Int
}