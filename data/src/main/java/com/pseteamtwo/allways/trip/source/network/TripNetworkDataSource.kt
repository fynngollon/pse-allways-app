package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

/**
 * This class handles all interactions with the trips network database.
 *
 * @constructor Creates an instance of the class
 */
interface TripNetworkDataSource {
    /**
     * Load trips searches in the database for all NetworkTrip int the table identified by the
     * given pseudonym.
     * It creates a connection to a MySql-server and executes the load-sql-statement.
     * The given variables are then added to the NetworkTrip which is added to a list
     * if the action was successful. If not a error massage will be thrown.
     * Then the list of questions will be returned.
     *
     * @param pseudonym Is used do identify the right table the stages are stored in.
     * @return Is the list of NetWorkTrips loaded from the table.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadTrips(pseudonym: String): List<NetworkTrip>

    /**
     * Save trips stores all NetworkTrips from the given list in the database.
     * It creates a connection to a MySql-server and executes the save-sql-statement.
     * If it did not work a error massage will be thrown.
     *
     * @param pseudonym Is used do identify the right table the trips are stored in.
     * @param trips Is the list of NetworkTrips that is stored in the table.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun saveTrips(pseudonym: String, trips: List<NetworkTrip>)

    /**
     * Delete trip deletes the given Trips from the database.
     * It creates a connection to a MySql-server and executes the delete-sql-statement.
     * If the action was successful the trips are no longer in the database.
     * If not a error massage will be thrown.
     *
     * @param pseudonym Is used do identify the right table the trips are stored in.
     * @param id Is the primary key to find the trip in the database.
     * @return 1 if the action was successful 0 if not.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteTrip(pseudonym: String, id: String): Int
}