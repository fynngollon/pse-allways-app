package com.pseteamtwo.allways.network

import java.sql.Connection
import java.sql.DriverManager

abstract class BaseNetworkDataSource {
    fun createConnection(): Connection {
        val url = "jdbc:postgresql://localhost:3306/allways-app"
        val username = "AppUser"
        val password = "Allways#App"

        return DriverManager.getConnection(url, username, password)
    }
}