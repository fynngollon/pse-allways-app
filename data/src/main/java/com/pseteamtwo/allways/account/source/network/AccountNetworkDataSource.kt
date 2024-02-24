package com.pseteamtwo.allways.account.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

/**
 * This class handles all interactions with the account network database.
 *
 */
interface AccountNetworkDataSource {
    /**
     * Load account searches in the database for the NetworkAccount with the given email.
     * It creates a connection to a MySql-server and executes the load-sql-statement.
     * The given variables are then added to the NetworkAccount which is returned if the action was
     * successful. If not a error massage will be thrown.
     *
     * @param email Is the primary-key to search for the NetworkAccount in the data-bank.
     * @return NetworkAccount from the database with the given email.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadAccount(email: String): NetworkAccount

    /**
     * Save account stores the given account and its variables in a database.
     * It creates a connection to a MySql-server and executes the save-sql-statement.
     * The variables of the account are then written in the accounts table of the database.
     * If it did not work a error massage will be thrown.
     * After saving the account for the first time it will also create all needed Tables
     * for the data of the account in a different database.
     * These tables hold the trips, stages, householdQuestions and profileQuestions of a account.
     * If that did not work a error massage will be thrown.
     *
     * @param account Is the account that is going to be stored in the database.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun saveAccount(account: NetworkAccount)

    /**
     * Delete account deletes the given account from the database.
     * It creates a connection to a MySql-server and executes the delete-sql-statement.
     * If the action was successful the account is no longer in the database.
     * If not a error massage will be thrown.
     *
     * @param account Is the account that is going to be deleted from the database.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun deleteAccount(account: NetworkAccount)

    /**
     * Does email exist searches in the database if a account with the given email exists
     * in the database.
     * It creates a connection to a MySql-server and executes the search-sql-statement.
     * It then returns if the email was found or not.
     * If it did not work a error massage will be thrown.
     *
     * @param email Is the email of the account that is searched for in the database.
     * @return True if the email exists and false if not.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun doesEmailExist(email: String): Boolean

    /**
     * Does pseudonym exist searches in the database if a account with the given pseudonym exists
     * in the database.
     * It creates a connection to a MySql-server and executes the search-sql-statement.
     * It then returns if the pseudonym was found or not.
     * If it did not work a error massage will be thrown.
     *
     * @param pseudonym Is the pseudonym of the account that is searched for in the database.
     * @return True if the pseudonym exists and false if not.
     */
    @Throws(ServerConnectionFailedException::class)
    suspend fun doesPseudonymExist(pseudonym: String): Boolean
}