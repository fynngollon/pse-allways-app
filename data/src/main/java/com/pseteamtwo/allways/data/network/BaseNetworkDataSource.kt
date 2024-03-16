package com.pseteamtwo.allways.data.network

import com.pseteamtwo.allways.data.exception.ServerConnectionFailedException
import java.sql.Connection
import java.sql.DriverManager

/**
 * This Class if for creating a connection to a MySql server.
 */
abstract class BaseNetworkDataSource {

    companion object {
        private const val MSG_CONNECTION_FAILED = "There is no connection to the network" +
                " database or it could not be established."
    }

    /**
     * Create account connection creates a connection to the account database.
     *
     * @return The made connection to prepare statements on.
     * @throws ServerConnectionFailedException If the connection could not be established.
     */
    @Throws(ServerConnectionFailedException::class)
    fun createRemoteAccountConnection() : Connection {
        val url = "jdbc:mysql://192.168.53.194:3306/allways-app-accounts"
        val username = "RemoteUser"
        val password = "Allways#App"

        try {
            return DriverManager.getConnection(url, username, password)
        } catch (e: Exception) {
            throw ServerConnectionFailedException(MSG_CONNECTION_FAILED)
        }
    }

    /**
     * Create data connection creates a connection to the data database.
     *
     * @return The made connection to prepare statements on.
     * @throws ServerConnectionFailedException If the connection could not be established.
     */
    @Throws(ServerConnectionFailedException::class)
    fun createRemoteDataConnection(): Connection {
        val url = "jdbc:mysql://192.168.53.194:3306/allways-app"
        val username = "RemoteUser"
        val password = "Allways#App"

        try {
            return DriverManager.getConnection(url, username, password)
        } catch (e: Exception) {
            throw ServerConnectionFailedException(MSG_CONNECTION_FAILED)
        }
    }
}