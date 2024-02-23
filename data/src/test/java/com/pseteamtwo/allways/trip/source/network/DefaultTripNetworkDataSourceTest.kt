package com.pseteamtwo.allways.trip.source.network

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.sql.SQLException

class DefaultTripNetworkDataSourceTest {

    private lateinit var defaultTripNetworkDataSource: DefaultTripNetworkDataSource

    @Before
    fun setUp() {
        defaultTripNetworkDataSource = DefaultTripNetworkDataSource()
    }
    @Test
    fun loadTrips() {
    }

    @Test
    fun saveTrips() {
    }

    @Test
    fun deleteTrip() {
        runBlocking {
            try {
                defaultTripNetworkDataSource.deleteTrip("kdb", "lol")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}