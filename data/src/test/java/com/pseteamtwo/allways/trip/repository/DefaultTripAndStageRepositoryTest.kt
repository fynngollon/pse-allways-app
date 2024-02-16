package com.pseteamtwo.allways.trip.repository

import android.location.Location
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.network.StageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.TripNetworkDataSource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.junit.Before
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class DefaultTripAndStageRepositoryTest {

    // Mock dependencies
    private val tripLocalDataSource = mockk<TripDao>()
    private val tripNetworkDataSource = mockk<TripNetworkDataSource>()
    private val stageLocalDataSource = mockk<StageDao>()
    private val stageNetworkDataSource = mockk<StageNetworkDataSource>()
    private val gpsPointLocalDataSource = mockk<GpsPointDao>()
    private val accountRepository = mockk<AccountRepository>()
    private val dispatcher = StandardTestDispatcher()

    // The class under test
    private lateinit var repository: DefaultTripAndStageRepository

    @Before
    fun setUp() {
        repository = DefaultTripAndStageRepository(
            tripLocalDataSource,
            tripNetworkDataSource,
            stageLocalDataSource,
            stageNetworkDataSource,
            gpsPointLocalDataSource,
            accountRepository,
            dispatcher
        )
    }

    fun createRandomLocation(): Location {
        val location = mockk<Location>()

        // Mocking behavior to return random latitude and longitude
        every { location.latitude } returns Random.nextDouble() * 180 - 90
        every { location.longitude } returns Random.nextDouble() * 360 - 180

        return location
    }

    @Test
    fun `create a valid gpsPoints`() = runTest(dispatcher) {
        val locations = mutableListOf<Location>()
        for (i in 0 until 10) {
            locations.add(createRandomLocation())
        }

        locations.forEach {
            println("Latitude: ${it.latitude}, Longitude: ${it.longitude}, Speed: ${it.speed}, Time: ${it.time}")
        }
    }




}