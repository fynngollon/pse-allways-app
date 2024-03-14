package com.pseteamtwo.allways.data.trip.repository


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.data.account.repository.AccountRepository
import com.pseteamtwo.allways.data.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.data.account.source.local.AccountDao
import com.pseteamtwo.allways.data.account.source.local.AccountDatabase
import com.pseteamtwo.allways.data.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.data.exception.NoTimeContinuityException
import com.pseteamtwo.allways.data.exception.TeleportationException
import com.pseteamtwo.allways.data.exception.TimeTravelException
import com.pseteamtwo.allways.data.trip.GpsPoint
import com.pseteamtwo.allways.data.trip.Mode
import com.pseteamtwo.allways.data.trip.Purpose
import com.pseteamtwo.allways.data.trip.Stage
import com.pseteamtwo.allways.data.trip.Trip
import com.pseteamtwo.allways.data.trip.convertToLocalDateTime
import com.pseteamtwo.allways.data.trip.source.local.GpsPointDao
import com.pseteamtwo.allways.data.trip.source.local.StageDao
import com.pseteamtwo.allways.data.trip.source.local.TripAndStageDatabase
import com.pseteamtwo.allways.data.trip.source.local.TripDao
import com.pseteamtwo.allways.data.trip.source.network.DefaultStageNetworkDataSource
import com.pseteamtwo.allways.data.trip.source.network.DefaultTripNetworkDataSource
import com.pseteamtwo.allways.data.trip.toLocation
import com.pseteamtwo.allways.data.trip.toExternal
import com.pseteamtwo.allways.data.trip.toLocal
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
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
* Instrumented test, which will execute on an Android device.
*
* This test tests [com.pseteamtwo.allways.data.trip.repository.DefaultTripAndStageRepository].
*/
@RunWith(AndroidJUnit4::class)
class DefaultTripAndStageRepositoryTest {

    //Test data
    private val geoPoint1 = GeoPoint(0.000, 0.001)
    private val geoPoint2 = GeoPoint(0.000, 0.002)
    private val geoPoint3 = GeoPoint(0.000, 0.002)
    private val geoPoint4 = GeoPoint(0.000, 0.003)
    private val geoPoint5 = GeoPoint(0.000, 0.003)
    private val geoPoint6 = GeoPoint(0.000, 0.004)

    private val time1: Long = 0
    private val time2: Long = 10
    private val time3: Long = 20
    private val time4: Long = 50
    private val time5: Long = 60
    private val time6: Long = 80
    private val futureTime: LocalDateTime = LocalDateTime.of(2050, 1, 1, 0, 0)
    //this start time has to be less than [time2]
    private val illegalStartTime1: Long = 5

    private val stage1 = Stage(
        1,
        Mode.WALK,
        listOf(
            GpsPoint(1, geoPoint1, time1.convertToLocalDateTime()),
            GpsPoint(2, geoPoint2, time2.convertToLocalDateTime())
        )
    )
    private val stage2 = Stage(
        2,
        Mode.MOTORCYCLE,
        listOf(
            GpsPoint(3, geoPoint3, time3.convertToLocalDateTime()),
            GpsPoint(4, geoPoint4, time4.convertToLocalDateTime())
        )
    )
    private val stage3 = Stage(
        3,
        Mode.MOTORCYCLE,
        listOf(
            GpsPoint(5, geoPoint5, time5.convertToLocalDateTime()),
            GpsPoint(6, geoPoint6, time6.convertToLocalDateTime())
        )
    )

    private val illegalStageModeNone = stage2.copy(mode = Mode.NONE)
    private val illegalStageWithTimeInFuture = Stage(
        2,
        Mode.LONG_DISTANCE_TRAIN,
        listOf(
            GpsPoint(3, geoPoint3, time3.convertToLocalDateTime()),
            GpsPoint(4, geoPoint4, futureTime)
        )
    )
    private val illegalStageWithNoDuration = Stage(
        2,
        Mode.BICYCLE,
        listOf(
            GpsPoint(3, geoPoint3, time3.convertToLocalDateTime()),
            GpsPoint(4, geoPoint4, time3.convertToLocalDateTime())
        )
    )
    private val illegalStageWithNoDistance = Stage(
        2,
        Mode.BICYCLE,
        listOf(
            GpsPoint(3, geoPoint3, time3.convertToLocalDateTime()),
            GpsPoint(4, geoPoint3, time4.convertToLocalDateTime())
        )
    )
    //this stage has an invalid startGpsPoint because its temporally before the endPoint of [stage1]
    private val illegalStageNoTimeContinuity = Stage(
        2,
        Mode.CAR_DRIVER,
        listOf(
            GpsPoint(3, geoPoint3, illegalStartTime1.convertToLocalDateTime()),
            GpsPoint(4, geoPoint4, time4.convertToLocalDateTime())
        )
    )
    //this stage has an invalid startGpsPoint because its locally not at endPoint of [stage1]
    private val illegalStageTeleportation = Stage(
        2,
        Mode.CAR_DRIVER,
        listOf(
            GpsPoint(3, geoPoint1, time3.convertToLocalDateTime()),
            GpsPoint(4, geoPoint4, time4.convertToLocalDateTime())
        )
    )

    private val userTrip1 = Trip(
        1,
        Purpose.WORK,
        true,
        listOf(stage1, stage2)
    )
    private val longerUserTrip1 = Trip(
        1,
        Purpose.WORK,
        true,
        listOf(stage1, stage2, stage3)
    )
    private val separatedUserTrip = Trip(
        2,
        Purpose.WORK,
        true,
        listOf(stage3)
    )
    private val otherPurpose = Purpose.EDUCATION



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
            testDispatcher,
            testScope
        )

    }



    @Test
    fun createTripStagesGpsPointsTest() = runTest {
        val gps1 = repository.createGpsPoint(geoPoint1.toLocation(time1))
        val gps2 = repository.createGpsPoint(geoPoint2.toLocation(time2))
        val gps3 = repository.createGpsPoint(geoPoint3.toLocation(time3))
        val gps4 = repository.createGpsPoint(geoPoint4.toLocation(time4))

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
        val insertedTrip = tripDao.getTripWithStages(userTrip1.id)!!
        assertEquals(userTrip1.copy(isConfirmed = false), insertedTrip.toExternal())
    }
    @Test(expected = IllegalArgumentException::class)
    fun createTripOfStageNotInDatabase() = runTest {
        //create a trip consisting of a stage which is not inside the local database already
        repository.createTripOfExistingStages(listOf(stage1).toLocal(null), userTrip1.purpose)
    }
    @Test
    fun createTripOfStageWithAlreadyAssignedTrip() = runTest {
        repository.createTrip(userTrip1.stages, userTrip1.purpose)

        val exception = try {
            repository.createTripOfExistingStages(
                listOf(stage1).toLocal(userTrip1.id),
                userTrip1.purpose
            )
            null
        } catch(e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "Any given stage is already assigned to another trip.",
            exception?.message
        )
    }



    @Test
    fun createTripTest() = runTest {
        repository.createTrip(userTrip1.stages, userTrip1.purpose)

        val savedUserTrip = repository.observeAllTrips().first().first()
        assertEquals(userTrip1, savedUserTrip)
    }
    @Test
    fun createTripIllegalPurpose() = runTest {
        val exception = try {
            repository.createTrip(userTrip1.stages, Purpose.NONE)
            null
        } catch(e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "Provided purpose cannot be NONE.",
            exception?.message
        )
    }
    @Test
    fun createTripIllegalSecondStageMode() = runTest {
        val exception = try {
            repository.createTrip(listOf(stage1, illegalStageModeNone), userTrip1.purpose)
            null
        } catch(e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "Provided stages are invalid.",
            exception?.message
        )
    }
    @Test
    fun createTripIllegalTimeInFuture() = runTest {
        val exception = try {
            repository.createTrip(
                listOf(stage1, illegalStageWithTimeInFuture), userTrip1.purpose
            )
            null
        } catch(e: TimeTravelException) {
            e
        }
        assertEquals(
            "At least 1 stage contains gpsPoints with times in the future which is invalid.",
            exception?.message
        )
    }
    @Test
    fun createTripIllegalDuration() = runTest {
        val exception = try {
            repository.createTrip(
                listOf(stage1, illegalStageWithNoDuration), userTrip1.purpose
            )
            null
        } catch(e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "At least 1 stage has a duration of 0.",
            exception?.message
        )
    }
    @Test
    fun createTripIllegalDistance() = runTest {
        val exception = try {
            repository.createTrip(
                listOf(stage1, illegalStageWithNoDistance), userTrip1.purpose
            )
            null
        } catch(e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "At least 1 stage has a distance of 0.",
            exception?.message
        )
    }
    @Test
    fun createTripIllegalTimeContinuityBetweenStages() = runTest {
        val exception = try {
            repository.createTrip(
                listOf(stage1, illegalStageNoTimeContinuity), userTrip1.purpose
            )
            null
        } catch(e: NoTimeContinuityException) {
            e
        }
        assertEquals(
            "No time continuity between stages.",
            exception?.message
        )
    }
    @Test
    fun createTripIllegalTeleportationBetweenStages() = runTest {
        val exception = try {
            repository.createTrip(
                listOf(stage1, illegalStageTeleportation), userTrip1.purpose
            )
            null
        } catch(e: TeleportationException) {
            e
        }
        assertEquals(
            "Not the same location between stages.",
            exception?.message
        )
    }
    @Test
    fun createTripInterferingWithOtherTripsAlreadyExistentInDatabase() = runTest {
        //this test needs a trip already inside the local database
        repository.createTrip(userTrip1.stages, userTrip1.purpose)

        val exception = try {
            repository.createTrip(userTrip1.stages, Purpose.OTHER)
            null
        } catch (e: TimeTravelException) {
            e
        }
        assertEquals(
            "Entered trip interferes with other trips already existent in the local database.",
            exception?.message
        )
    }



    @Test
    fun updateTripPurpose() = runTest {
        createTripTest()
        repository.updateTripPurpose(userTrip1.id, otherPurpose)

        val loadedTrip = tripDao.getTripWithStages(userTrip1.id)!!
        assertEquals(
            userTrip1.copy(purpose = otherPurpose),
            loadedTrip.toExternal()
        )
    }
    @Test
    fun updateTripPurposeNoneSoTripGetsUnconfirmed() = runTest {
        createTripTest()
        repository.updateTripPurpose(userTrip1.id, Purpose.NONE)

        val loadedTrip = tripDao.getTripWithStages(userTrip1.id)!!
        assertEquals(
            userTrip1.copy(purpose = Purpose.NONE, isConfirmed = false),
            loadedTrip.toExternal()
        )
    }



    @Test
    fun separateStageFromTripTest() = runTest {
        repository.createTrip(longerUserTrip1.stages, longerUserTrip1.purpose)

        repository.separateStageFromTrip(longerUserTrip1.stages.last().id)
        val allTripsOnLocalDatabase = tripDao.getAllTripsWithStages()
        assertEquals(2, allTripsOnLocalDatabase.size)
        assertEquals(userTrip1, allTripsOnLocalDatabase[0].toExternal())
        assertEquals(separatedUserTrip, allTripsOnLocalDatabase[1].toExternal())
    }
    @Test
    fun separateMiddleStage() = runTest {
        repository.createTrip(longerUserTrip1.stages, longerUserTrip1.purpose)

        val exception = try {
            repository.separateStageFromTrip(longerUserTrip1.stages[1].id)
            null
        } catch (e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "Cannot separate stage which is not the beginning or" +
                    " ending stage of the trip in this version of the app.",
            exception?.message
        )
    }



    @Test
    fun deleteTripTest() = runTest {
        createTripTest()
        assertEquals(1, repository.observeAllTrips().first().size)
        repository.deleteTrip(userTrip1.id)
        assertEquals(0, repository.observeAllTrips().first().size)
        assertEquals(0, stageDao.getAll().size)
        assertEquals(0, gpsPointDao.observeAll().first().size)
    }



    @Test
    fun deleteStageTest() = runTest {
        createTripTest()

        repository.deleteStage(userTrip1.stages.first().id)
        val allStagesOnLocalDatabase = stageDao.getAll()
        assertEquals(1, allStagesOnLocalDatabase.size)
        val onlyStageOnLocalDatabase =
            stageDao.getStageWithGpsPoints(allStagesOnLocalDatabase.first().id)!!
        assertEquals(stage2, onlyStageOnLocalDatabase.toExternal())
    }
    @Test
    fun deleteMiddleStage() = runTest {
        repository.createTrip(longerUserTrip1.stages, longerUserTrip1.purpose)

        val exception = try {
            repository.deleteStage(longerUserTrip1.stages[1].id)
            null
        } catch (e: IllegalArgumentException) {
            e
        }
        assertEquals(
            "Cannot delete stage which is not the beginning or" +
                    " ending stage of the trip in this version of the app.",
            exception?.message
        )
    }



    @Test
    fun connectTripsTest() = runTest {
        repository.createTrip(userTrip1.stages, userTrip1.purpose)
        repository.createTrip(separatedUserTrip.stages, separatedUserTrip.purpose)
        var allTripsOnLocalDatabase = tripDao.getAllTripsWithStages()
        assertEquals(2, allTripsOnLocalDatabase.size)

        repository.connectTrips(listOf(userTrip1.id, separatedUserTrip.id))
        allTripsOnLocalDatabase = tripDao.getAllTripsWithStages()
        assertEquals(1, allTripsOnLocalDatabase.size)
        assertEquals(
            longerUserTrip1.copy(id = 3, purpose = Purpose.NONE, isConfirmed = false),
            allTripsOnLocalDatabase.first().toExternal()
        )
    }



    @Test
    fun getTripsOfDateTest() = runTest {
        createTripTest()
        val startDate = LocalDate.of(1970, 1, 1)
        val loadedTrips = repository.getTripsOfDate(startDate)
        assertEquals(1, loadedTrips.size)
        assertEquals(userTrip1, loadedTrips.first())
    }
    @Test
    fun getTripsOfDateButNoTripsAreOnThatDate() = runTest {
        createTripTest()
        val startDate = LocalDate.of(1970, 1, 2)
        val loadedTrips = repository.getTripsOfDate(startDate)
        assertEquals(0, loadedTrips.size)
    }

    @Test
    fun getTripsOfTimespanTest() = runTest {
        createTripTest()
        val startOfTimespan = 0L.convertToLocalDateTime()
        val endOfTimespan = 1L.convertToLocalDateTime()
        val loadedTrips = repository.getTripsOfTimespan(startOfTimespan, endOfTimespan)
        assertEquals(1, loadedTrips.size)
        assertEquals(userTrip1, loadedTrips.first())
    }



    @Test
    fun saveTripsAndStagesToNetwork() {
        TODO("Not yet implemented")
    }
}
