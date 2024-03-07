package com.pseteamtwo.allways.trip.source.local

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.toExternal
import com.pseteamtwo.allways.trip.toLocation
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint

/**
 * Instrumented test, which will execute on an Android device.
 *
 * This test tests [com.pseteamtwo.allways.trip.source.local.TripDao]
 * and [com.pseteamtwo.allways.trip.source.local.TripAndStageDatabase].
 */
@RunWith(AndroidJUnit4::class)
class TripDaoTest {
    //Test data
    private val geoPoint1 = GeoPoint(0.000, 0.001)
    private val geoPoint2 = GeoPoint(0.000, 0.002)
    private val geoPoint3 = GeoPoint(0.000, 0.003)

    private val location1 = LocalGpsPoint(location = (geoPoint1.toLocation(0)))
    private val location2 = LocalGpsPoint(location = (geoPoint2.toLocation(10)))
    private val location3 = LocalGpsPoint(location = (geoPoint3.toLocation(20)))

    private val stage1 = LocalStage(
        1000,
        null,
        Mode.WALK
    )
    private val stage2 = LocalStage(
        1001,
        null,
        Mode.MOTORCYCLE
    )

    // using an in-memory database because the information stored here disappears when the
    // process is killed
    private lateinit var database: TripAndStageDatabase


    // Ensure that we use a new database for each test.
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TripAndStageDatabase::class.java
        ).allowMainThreadQueries().build()
    }


    @Test
    fun insertTripAndGetBack() = runTest {
        // GIVEN - insert a trip
        val trip = LocalTrip(
            purpose = Purpose.WORK,
            isConfirmed = false
        )
        val tripId = database.tripDao().insert(trip)

        val stageId = database.stageDao().insert(stage1.copy(tripId = tripId))

        val gspPoint1Id = database.gpsPointDao().insert(location1.copy(stageId = stageId))
        val gspPoint2Id = database.gpsPointDao().insert(location2.copy(stageId = stageId))

        val allLoaded = database.tripDao().observeAll().first()
        assertEquals(1, allLoaded.size)

        // WHEN - Get the trip by id from the database
        val loadedTripWithStages = database.tripDao().getTripWithStages(tripId)

        // THEN - The loaded data contains the expected values
        assertNotNull(loadedTripWithStages as LocalTripWithStages)
        assertEquals(1, loadedTripWithStages.stages.size)
        assertEquals(2, loadedTripWithStages.stages.first().gpsPoints.size)

        Log.d("LogTest", loadedTripWithStages.toExternal().toString())
    }
}