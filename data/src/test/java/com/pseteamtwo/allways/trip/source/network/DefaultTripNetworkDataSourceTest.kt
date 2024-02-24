package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.trip.Purpose
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime
import java.sql.SQLException

class DefaultTripNetworkDataSourceTest {

    private lateinit var defaultTripNetworkDataSource: DefaultTripNetworkDataSource
    private lateinit var trip1: NetworkTrip
    private lateinit var trip2: NetworkTrip
    private lateinit var trips: List<NetworkTrip>
    private lateinit var stageIds: List<Long>


    @Before
    fun setUp() {
        defaultTripNetworkDataSource = DefaultTripNetworkDataSource()
        stageIds = listOf(12, 11, 10)
        trip1 = NetworkTrip(
            123L,
            stageIds,
            Purpose.EDUCATION,
            LocalDateTime.of(2001,11,9,11,2, 14),
            LocalDateTime.of(2001,11,9,11,4,14),
            GeoPoint(0.01,0.3),
            GeoPoint(1.23,32.5),
            12,
            23
        )

        trip2 = NetworkTrip(
            122L,
            stageIds,
            Purpose.BUSINESS_TRIP,
            LocalDateTime.of(2001,11,9,11,12),
            LocalDateTime.of(2001,11,9,11,40),
            GeoPoint(0.01,0.3),
            GeoPoint(1.23,32.5),
            12,
            23
        )

        trips = listOf(trip1, trip2)
    }

    @Test
    fun loadTrips() {
        runBlocking {
            try {
                defaultTripNetworkDataSource.loadTrips("kdb")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun saveTrips() {
        runBlocking {
            try {
                defaultTripNetworkDataSource.saveTrips("kdb", trips)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun deleteTrip() {
        runBlocking {
            try {
                defaultTripNetworkDataSource.deleteTrip("kdb", "123")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}