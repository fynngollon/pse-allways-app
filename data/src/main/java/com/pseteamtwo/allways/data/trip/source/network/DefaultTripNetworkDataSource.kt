package com.pseteamtwo.allways.data.trip.source.network

import android.util.Log
import com.pseteamtwo.allways.data.network.BaseNetworkDataSource
import com.pseteamtwo.allways.data.trip.Purpose
import kotlinx.coroutines.sync.Mutex

/**
 * This class implements the [TripNetworkDataSource].
 *
 * @constructor Creates an instance of the class.
 */
class DefaultTripNetworkDataSource : TripNetworkDataSource, BaseNetworkDataSource() {
    private val accessMutex = Mutex()

    override suspend fun loadTrips(pseudonym: String): List<NetworkTrip> {
        accessMutex.lock() // Acquire lock to ensure thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            connection.use {
                // Prepare and execute SQL statement to retrieve all trips
                val sqlLoadStatement = "SELECT * FROM `allways-app`.`%s`;"
                val statement = connection.prepareStatement(sqlLoadStatement.format("tbl${pseudonym}trips"))
                val resultSet = statement.executeQuery()

                // Extract data from the result set and convert to NetworkTrip objects
                val trips = mutableListOf<NetworkTrip>()
                while (resultSet.next()) {
                    val trip = NetworkTrip(
                        resultSet.getString("id").toLong(),
                        fromStringToListOfStageIds(resultSet.getString("stageIds")),
                        Purpose.valueOf(resultSet.getString("purpose")),
                        fromStringToLocalDateTime(resultSet.getString("startDateTime")),
                        fromStringToLocalDateTime(resultSet.getString("endDateTime")),
                        fromStringToGeoPoint(resultSet.getString("startLocation")) ,
                        fromStringToGeoPoint(resultSet.getString("endLocation")) ,
                        resultSet.getInt("duration"),
                        resultSet.getInt("distance")
                    )
                    trips.add(trip)
                }

                // Close the result set before closing the statement
                resultSet.close()
                statement.close()

                return trips
            }

        } catch (e: Exception) {
            // Handle database errors (e.g., connection issues)
            throw Exception("Failed to load all trips", e)
        } finally {
            accessMutex.unlock() // Release lock after operation
        }
    }

    override suspend fun saveTrips(pseudonym: String, trips: List<NetworkTrip>) {
        accessMutex.lock() // Acquire lock for thread safety

        try {
            // Connect to the MySQL database
            val connection = createDataConnection()

            try {
                connection.use {
                    // Prepare a single parameterized statement for efficiency
                    val statement = connection.prepareStatement("INSERT INTO `allways-app`.`tbl${pseudonym}trips` " +
                            "(id, stageIds, purpose, startDateTime, endDateTime, duration, distance, startLocation, endLocation) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")

                    // Execute for each trip using bind variables and handle potential errors
                    for (trip in trips) {
                        statement.setString(1, trip.id.toString())
                        statement.setString(2, trip.stageIds.joinToString (","))
                        statement.setString(3, trip.purpose.toString())
                        statement.setString(4, trip.startDateTime.toString())
                        statement.setString(5, trip.endDateTime.toString())
                        statement.setInt(6,trip.duration)
                        statement.setInt(7, trip.distance)
                        statement.setString(8, trip.startLocation.toString())
                        statement.setString(9, trip.endLocation.toString())

                        try {
                            statement.addBatch()
                        } catch (e: Exception) {
                            // Handle individual trip insertion error gracefully (e.g., log, skip to next trip)
                            Log.e("DefaultTripNetworkDataSource", "Error preparing trip ${trip.id}: ${e.message}")
                        }
                    }

                    // Execute the batch insert and handle overall execution errors
                    try {
                        statement.executeBatch()
                    } catch (e: Exception) {
                        // Handle batch execution errors (e.g., network issues, connection problems)
                        throw Exception("Failed to save trips: ${e.message}", e)
                    }
                }
            } finally {
                accessMutex.unlock() // Release lock after operation
            }
        } catch (e: Exception) {
            // Handle overall database connection issues or unexpected errors
            throw Exception("Critical error saving trips: ${e.message}", e)
        }
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