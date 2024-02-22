package com.pseteamtwo.allways.trip.repository

import android.location.Location
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.AccountDatabase
import com.pseteamtwo.allways.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.LocalStage
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.local.TripDatabase
import com.pseteamtwo.allways.trip.source.network.DefaultStageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.DefaultTripNetworkDataSource
import com.pseteamtwo.allways.trip.toLocal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint

/**
* Instrumented test, which will execute on an Android device.
*
* This test tests [com.pseteamtwo.allways.trip.repository.DefaultTripAndStageRepository].
*/
@RunWith(AndroidJUnit4::class)
class DefaultTripAndStageRepositoryTest {

    //Test data
    private val geoPoint1 = GeoPoint(0.000, 0.001)
    private val geoPoint2 = GeoPoint(0.000, 0.002)
    private val geoPoint3 = GeoPoint(0.000, 0.003)

    private val location1 = GpsPoint(0, (geoPoint1.toLocation(0)))
    private val location2 = GpsPoint(1, (geoPoint2.toLocation(10)))
    private val location3 = GpsPoint(2, (geoPoint3.toLocation(20)))

    private val stage1 = Stage(
        1000,
        Mode.WALK,
        listOf(location1, location2)
    )
    private val stage2 = Stage(
        1001,
        Mode.MOTORCYCLE,
        listOf(location2, location3)
    )

    private val localStage1 = LocalStage(
        1000,
        null,
        Mode.WALK
    )
    private val localStage2 = LocalStage(
        1001,
        null,
        Mode.MOTORCYCLE
    )

    //Test dependencies
    private lateinit var accountRepository: AccountRepository

    private lateinit var database: TripDatabase
    private lateinit var accountDatabase: AccountDatabase

    private lateinit var tripDao: TripDao
    private lateinit var stageDao: StageDao
    private lateinit var gpsPointDao: GpsPointDao
    private lateinit var accountDao: AccountDao

    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    //Class under test
    private lateinit var repository: DefaultTripAndStageRepository


    // using an in-memory database because the information stored here disappears when the
    // process is killed
    // Ensure that we use a new database for each test.
    @Before
    fun createRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TripDatabase::class.java
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
            testDispatcher
        )

    }

    @Test
    fun createTripTest() = runTest {
        database.stageDao().insert(localStage1)
        database.stageDao().insert(localStage2)
        repository.createTrip(listOf(stage1, stage2), Purpose.WORK)

        val trips = tripDao.observeAll().first()
        val stages = stageDao.getAll()
        val gpsPoints = gpsPointDao.observeAll().first()

        assertEquals(1, trips.size)
        assertEquals(2, stages.size)
    }



    private fun GeoPoint.toLocation(time: Long): Location {
        val location = Location("osmdroid")
        location.latitude = this.latitude
        location.longitude = this.longitude
        location.time = time
        location.speed = 0f
        return location
    }
}