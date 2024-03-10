package com.pseteamtwo.allways.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import java.sql.Connection
import java.sql.DriverManager
import kotlin.jvm.Throws

/**
 * This Class if for creating a connection to a MySql server.
 *
 */
abstract class BaseNetworkDataSource {

    companion object {
        private const val CONNECTION_FAILED_MESSAGE = "There is no connection to the network" +
                " database or it could not be established."
    }

    /**
     * Create account connection creates a connection to the account database.
     *
     * @return The made connection to prepare statements on.
     * @throws ServerConnectionFailedException If the connection could not be established.
     */
    @Throws(ServerConnectionFailedException::class)
    fun createAccountConnection() : Connection {
        val url = "jdbc:mysql://127.0.0.1:3306/allways-app-accounts"
        val username = "AppUser"
        val password = "Allways#App"

        try {
            return DriverManager.getConnection(url, username, password)
        } catch (e: Exception) {
            throw ServerConnectionFailedException(CONNECTION_FAILED_MESSAGE)
        }
    }

    /**
     * Create data connection creates a connection to the data database.
     *
     * @return The made connection to prepare statements on.
     * @throws ServerConnectionFailedException If the connection could not be established.
     */
    @Throws(ServerConnectionFailedException::class)
    fun createDataConnection(): Connection {
        val url = "jdbc:mysql://127.0.0.1:3306/allways-app"
        val username = "AppUser"
        val password = "Allways#App"

        try {
            return DriverManager.getConnection(url, username, password)
        } catch (e: Exception) {
            throw ServerConnectionFailedException(CONNECTION_FAILED_MESSAGE)
        }
    }
}