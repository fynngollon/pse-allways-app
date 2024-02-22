package com.pseteamtwo.allways.network

import java.sql.Connection
import java.sql.DriverManager

abstract class BaseNetworkDataSource {
    fun createAccountConnection(): Connection {
        val url = "jdbc:mysql://127.0.0.1:3306/allways-app-accounts"
        val username = "AppUser"
        val password = "Allways#App"

        return DriverManager.getConnection(url, username, password)
    }

    fun createDataConnection(): Connection {
        val url = "jdbc:mysql://127.0.0.1:3306/allways-app"
        val username = "AppUser"
        val password = "Allways#App"

        return DriverManager.getConnection(url, username, password)
    }
}