package com.pseteamtwo.allways.network

import java.sql.Connection
import java.sql.DriverManager

abstract class BaseNetworkDataSource {
    fun createConnection(): Connection {
        val url = "jdbc:mysql://localhost:3306/MySQL80"
        val username = "root"
        val password = "Allways#App"

        return DriverManager.getConnection(url, username, password)
    }
}