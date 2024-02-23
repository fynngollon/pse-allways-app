package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.network.BaseNetworkDataSource
import kotlinx.coroutines.sync.Mutex

class DefaultStageNetworkDataSource : StageNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadStages(): List<NetworkStage> {
        TODO("Not yet implemented")
    }

    override suspend fun saveStages(stages: List<NetworkStage>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStage(pseudonym: String, id: String): Int {
        accessMutex.lock() // Acquire lock for thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare deletion statement with parameterized id
                val tableName = "tbl${pseudonym}stages"
                val sqlDeleteStatement = "DELETE FROM `allways-app`.`%s` WHERE (`id` = ?);"
                val statement = connection.prepareStatement(sqlDeleteStatement.format(tableName))
                statement.setString(1, id)
                statement.executeUpdate()
                statement.close()
                return 1
            }

        } catch (e: Exception) {
            throw Exception("Failed to delete stage with id: $id", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }
}