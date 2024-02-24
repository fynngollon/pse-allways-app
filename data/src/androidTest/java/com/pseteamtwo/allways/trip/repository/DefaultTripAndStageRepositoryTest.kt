package com.pseteamtwo.allways.trip.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.AccountDatabase
import com.pseteamtwo.allways.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.trip.source.local.StageDao
import com.pseteamtwo.allways.trip.source.local.TripDao
import com.pseteamtwo.allways.trip.source.local.TripAndStageDatabase
import com.pseteamtwo.allways.trip.source.network.DefaultStageNetworkDataSource
import com.pseteamtwo.allways.trip.source.network.DefaultTripNetworkDataSource
import com.pseteamtwo.allways.trip.toLocation
import com.pseteamtwo.allways.utils.TIME_1
import com.pseteamtwo.allways.utils.TIME_2
import com.pseteamtwo.allways.utils.TIME_3
import com.pseteamtwo.allways.utils.TIME_4
import com.pseteamtwo.allways.utils.geoPoint1
import com.pseteamtwo.allways.utils.geoPoint2
import com.pseteamtwo.allways.utils.geoPoint3
import com.pseteamtwo.allways.utils.geoPoint4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
* Instrumented test, which will execute on an Android device.
*
* This test tests [com.pseteamtwo.allways.trip.repository.DefaultTripAndStageRepository].
*/
@RunWith(AndroidJUnit4::class)
class DefaultTripAndStageRepositoryTest {

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

    //Class under test
    private lateinit var repository: DefaultTripAndStageRepository


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
            testDispatcher
        )

    }

    @Test
    fun createTripStagesGpsPointsTest() = runTest {
        val gps1 = repository.createGpsPoint(geoPoint1.toLocation(TIME_1))
        val gps2 = repository.createGpsPoint(geoPoint2.toLocation(TIME_2))
        val gps3 = repository.createGpsPoint(geoPoint3.toLocation(TIME_3))
        val gps4 = repository.createGpsPoint(geoPoint4.toLocation(TIME_4))

        val createdStage1 =
            repository.createStageOfExistingGpsPoints(listOf(gps1, gps2), Mode.WALK)
        val createdStage2 =
            repository.createStageOfExistingGpsPoints(listOf(gps3, gps4), Mode.MOTORCYCLE)

        repository.createTripOfExistingStages(listOf(createdStage1, createdStage2), Purpose.WORK)

        val trips = tripDao.observeAll().first()
        val stages = stageDao.getAll()
        val gpsPoints = gpsPointDao.observeAll().first()

        assertEquals(1, trips.size)
        assertEquals(2, stages.size)
        assertEquals(4, gpsPoints.size)

        //assertEquals(stage1, createdStage1)
        //assertEquals(stage2, createdStage2)
    }
}