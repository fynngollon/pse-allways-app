package com.pseteamtwo.allways.trip.source.network

import android.util.Log
import com.pseteamtwo.allways.network.BaseNetworkDataSource
import com.pseteamtwo.allways.trip.Mode
import kotlinx.coroutines.sync.Mutex

internal class DefaultStageNetworkDataSource : StageNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadStages(pseudonym: String): List<NetworkStage> {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare and execute SQL statement to retrieve all questions
                val sqlLoadStatement = "SELECT * FROM `allways-app`.`%s`;"
                val statement = connection.prepareStatement(sqlLoadStatement.format("tbl${pseudonym}stages"))
                val resultSet = statement.executeQuery()

                // Extract data from the result set and convert to NetworkQuestion objects
                val stages = mutableListOf<NetworkStage>()
                while (resultSet.next()) {
                    val stage = NetworkStage(
                        resultSet.getString("id").toLong(),
                        resultSet.getString("tripId").toLong(),
                        Mode.valueOf(resultSet.getString("mode")),
                        fromStringToLocalDateTime(resultSet.getString("startDateTime")),
                        fromStringToLocalDateTime(resultSet.getString("endDateTime")),
                        fromStringToGeoPoint(resultSet.getString("startLocation")) ,
                        fromStringToGeoPoint(resultSet.getString("endLocation")) ,
                        resultSet.getInt("duration"),
                        resultSet.getInt("distance")
                    )
                    stages.add(stage)
                }

                // Close the result set before closing the statement
                resultSet.close()
                statement.close()

                return stages
            }

        } catch (e: Exception) {
            // Handle database errors (e.g., connection issues)
            throw Exception("Failed to load all stages", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun saveStages(pseudonym: String, stages: List<NetworkStage>) {
        accessMutex.lock() // Acquire lock for thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            try {
                connection.use {
                    // Prepare a single parameterized statement for efficiency
                    val statement = connection.prepareStatement("INSERT INTO `allways-app`.`tbl${pseudonym}stages` " +
                            "(id, tripId, mode, startDateTime, endDateTime, duration, distance, startLocation, endLocation) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")

                    // Execute for each stage using bind variables and handle potential errors
                    for (stage in stages) {
                        statement.setString(1, stage.id.toString())
                        statement.setString(2, stage.tripId.toString())
                        statement.setString(3, stage.mode.toString())
                        statement.setString(4, stage.startDateTime.toString())
                        statement.setString(5, stage.endDateTime.toString())
                        statement.setInt(6, stage.duration)
                        statement.setInt(7, stage.distance)
                        statement.setString(8, stage.startLocation.toString())
                        statement.setString(9, stage.endLocation.toString())

                        try {
                            statement.addBatch()
                        } catch (e: Exception) {
                            // Handle individual stage insertion error gracefully (e.g., log, skip to next stage)
                            Log.e("DefaultStageNetworkDataSource", "Error preparing stage ${stage.id}: ${e.message}")
                        }
                    }

                    // Execute the batch insert and handle overall execution errors
                    try {
                        statement.executeBatch()
                    } catch (e: Exception) {
                        // Handle batch execution errors (e.g., network issues, connection problems)
                        throw Exception("Failed to save stages: ${e.message}", e)
                    }
                }
            } finally {
                accessMutex.unlock() // Release lock after operation
            }
        } catch (e: Exception) {
            // Handle overall database connection issues or unexpected errors
            throw Exception("Critical error saving stages: ${e.message}", e)
        }
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