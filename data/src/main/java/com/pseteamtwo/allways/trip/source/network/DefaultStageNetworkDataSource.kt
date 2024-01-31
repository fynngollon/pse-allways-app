package com.pseteamtwo.allways.trip.source.network

import kotlinx.coroutines.sync.Mutex

class DefaultStageNetworkDataSource : StageNetworkDataSource {
    private val accessMutex = Mutex()

    override suspend fun loadStages(): List<NetworkStage> {
        TODO("Not yet implemented")
    }

    override suspend fun saveStages(stages: List<NetworkStage>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStage(id: String): Int {
        TODO("Not yet implemented")
    }
}