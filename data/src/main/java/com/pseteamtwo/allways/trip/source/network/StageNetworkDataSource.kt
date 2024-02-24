package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

/**
 * This class handles all interactions with the stages network database.
 *
 */
interface StageNetworkDataSource {
    /**
     * Load stages searches in the database for all NetworkStages int the table identified by the
     * given pseudonym.
     * It creates a connection to a MySql-server and executes the load-sql-statement.
     * The given variables are then added to the NetworkStage which is added to a list
     * if the action was successful. If not a error massage will be thrown.
     * Then the list of questions will be returned.
     *
     * @param pseudonym  Is used do identify the right table the stages are stored in.
     * @return Is the list of NetWorkStages loaded from the table.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadStages(pseudonym: String): List<NetworkStage>

    /**
     * Save stages stores all NetworkStages from the given list in the database.
     * It creates a connection to a MySql-server and executes the save-sql-statement.
     * If it did not work a error massage will be thrown.
     *
     * @param pseudonym Is used do identify the right table the stages are stored in.
     * @param stages Is the list of NetworkStages that is stored in the table.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun saveStages(pseudonym: String, stages: List<NetworkStage>)

    /**
     * Delete stage deletes the given Stages from the database.
     * It creates a connection to a MySql-server and executes the delete-sql-statement.
     * If the action was successful the stages are no longer in the database.
     * If not a error massage will be thrown.
     *
     * @param pseudonym Is used do identify the right table the stages are stored in.
     * @param id Is the primary key to find the stage in the database.
     * @return 1 if the action was successful 0 if not.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteStage(pseudonym: String, id: String): Int
}