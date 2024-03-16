package com.pseteamtwo.allways.data.trip.tracking

import android.location.Location
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.data.account.repository.AccountRepository
import com.pseteamtwo.allways.data.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.data.account.source.local.AccountDao
import com.pseteamtwo.allways.data.account.source.local.AccountDatabase
import com.pseteamtwo.allways.data.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.data.trip.repository.DefaultTripAndStageRepository
import com.pseteamtwo.allways.data.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.data.trip.source.local.StageDao
import com.pseteamtwo.allways.data.trip.source.local.TripAndStageDatabase
import com.pseteamtwo.allways.data.trip.source.local.TripDao
import com.pseteamtwo.allways.data.trip.source.network.DefaultStageNetworkDataSource
import com.pseteamtwo.allways.data.trip.source.network.DefaultTripNetworkDataSource
import com.pseteamtwo.allways.data.trip.tracking.algorithm.DefaultTrackingAlgorithm
import com.pseteamtwo.allways.data.trip.tracking.location.calculateSpeedBetweenLocations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultTrackingAlgorithmTest {

    private val locations = createLocations(50)

    private fun createLocations(amount: Int): List<Location> {
        val locations = mutableListOf<Location>()
        var longitude = 49.2400000
        var latitude = 8.0940000
        var time = 1710000000000
        for (i in 0 until amount) {
            val location = Location("fused")
            location.longitude = longitude
            location.latitude = latitude
            location.time = time
            location.speed = if (i - 1 < 0) 0F else calculateSpeedBetweenLocations(location, locations[i - 1])
            locations.add(location)
            longitude += 0.0003
            latitude += 0.001
            time += 30000
        }
        return locations
    }

    //Test dependencies
    private lateinit var accountRepository: AccountRepository

    private lateinit var database: TripAndStageDatabase
    private lateinit var accountDatabase: AccountDatabase

    private lateinit var tripDao: TripDao
    private lateinit var stageDao: StageDao
    private lateinit var gpsPointDao: GpsPointDao
    private lateinit var accountDao: AccountDao

    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    private lateinit var repository: DefaultTripAndStageRepository

    // Class under test
    private lateinit var trackingAlgorithm: DefaultTrackingAlgorithm



    // using an in-memory database because the information stored here disappears when the
    // process is killed
    // Ensure that we use a new database for each test.
    @Before
    fun createRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TripAndStageDatabase::class.java
        ).allowMainThreadQueries().build()

        accountDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AccountDatabase::class.java
        ).allowMainThreadQueries().build()

        tripDao = database.tripDao()
        stageDao = database.stageDao()
        gpsPointDao = database.gpsPointDao()
        accountDao = accountDatabase.accountDao()


        accountRepository = DefaultAccountRepository(
            accountDao,
            DefaultAccountNetworkDataSource(),
            testDispatcher
        )


        repository = DefaultTripAndStageRepository(
            tripDao,
            DefaultTripNetworkDataSource(),
            stageDao,
            DefaultStageNetworkDataSource(),
            gpsPointDao,
            accountRepository,
            testDispatcher,
            testScope
        )

        trackingAlgorithm = DefaultTrackingAlgorithm(
            repository,
            gpsPointDao,
            testDispatcher,
            testScope
        )

        runBlocking {
            locations.forEach {
                repository.createGpsPoint(it)
            }
        }

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun predictSimpleTripWithOneStage() = runBlocking {

        trackingAlgorithm.observeTrackingData()

        delay(1000)

        val gpsPoints = withContext(testDispatcher) { gpsPointDao.observeAll().first() }
        gpsPoints.forEach { println("ID: ${it.id}, stage: ${it.stageId}") }

        val stages = withContext(testDispatcher) { stageDao.getAll() }
        stages.forEach { println("ID: ${it.id}, trip: ${it.tripId}, mode: ${it.mode}") }

        val trips = repository.observeAllTrips().first()
        assert(trips.isNotEmpty()) { "Trip size is ${trips.size}" }
    }


}