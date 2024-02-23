package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.network.BaseNetworkDataSource
import kotlinx.coroutines.sync.Mutex

internal class DefaultTripNetworkDataSource : TripNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadTrips(): List<NetworkTrip> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTrips(trips: List<NetworkTrip>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(pseudonym: String, id: String): Int {
        accessMutex.lock() // Acquire lock for thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare deletion statement with parameterized id
                val tableName = "tbl${pseudonym}trips"
                val sqlDeleteStatement = "DELETE FROM `allways-app`.`%s` WHERE (`id` = ?);"
                val statement = connection.prepareStatement(sqlDeleteStatement.format(tableName))
                statement.setString(1, id)
                statement.executeUpdate()
                statement.close()
                return 1
            }

        } catch (e: Exception) {
            throw Exception("Failed to delete trip with id: $id", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }
}