package com.pseteamtwo.allways.trip.source.local

import android.location.Location
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.trip.GpsPoint
import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import com.pseteamtwo.allways.trip.Stage
import com.pseteamtwo.allways.trip.toLocal
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
 * This test tests [com.pseteamtwo.allways.trip.source.local.StageDao]
 * and [com.pseteamtwo.allways.trip.source.local.StageDatabase].
 */
@RunWith(AndroidJUnit4::class)
class StageDaoTest {
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
    private lateinit var database: TripDatabase


    // Ensure that we use a new database for each test.
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TripDatabase::class.java
        ).allowMainThreadQueries().build()
    }


    @Test
    fun insertStageAndGetBack() = runTest {
        // GIVEN - insert a stage
        val id = database.stageDao().insert(stage1)

        val allLoaded = database.stageDao().getAll()
        assertEquals(1, allLoaded.size)

        // WHEN - Get the stage by id from the database
        val loaded = database.stageDao().get(id)

        // THEN - The loaded data contains the expected values
        assertNotNull(loaded as LocalStage)
        assertEquals(id, loaded.id)
        assertEquals(stage1.tripId, loaded.tripId)
        assertEquals(stage1.mode, loaded.mode)
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