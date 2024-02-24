package com.pseteamtwo.allways.network

import java.sql.Connection
import java.sql.DriverManager

/**
 * This Class if for creating a connection to a MySql server.
 *
 */
abstract class BaseNetworkDataSource {
    /**
     * Create account connection creates a connection to the account database.
     *
     * @return The made connection to prepare statements on.
     */
    fun createAccountConnection(): Connection {
        val url = "jdbc:mysql://127.0.0.1:3306/allways-app-accounts"
        val username = "AppUser"
        val password = "Allways#App"

        return DriverManager.getConnection(url, username, password)
    }

    /**
     * Create data connection creates a connection to the data database.
     *
     * @return The made connection to prepare statements on.
     */
    fun createDataConnection(): Connection {
        val url = "jdbc:mysql://127.0.0.1:3306/allways-app"
        val username = "AppUser"
        val password = "Allways#App"

        return DriverManager.getConnection(url, username, password)
    }
}