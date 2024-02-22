package com.pseteamtwo.allways.trip.source.local

import android.location.Location
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
 * This test tests [com.pseteamtwo.allways.trip.source.local.GpsPointDao]
 * and [com.pseteamtwo.allways.trip.source.local.GpsPointDatabase].
 */
@RunWith(AndroidJUnit4::class)
class GpsPointDaoTest {
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
    fun insertGpsPointAndGetBack() = runTest {
        // GIVEN - insert a gpsPoint
        val gpsPoint = LocalGpsPoint(
            location = GeoPoint(0.000, 0.001).toLocation(0)
        )
        val id = database.gpsPointDao().insert(gpsPoint)

        val allLoaded = database.gpsPointDao().observeAll().first()
        assertEquals(1, allLoaded.size)

        // WHEN - Get the gpsPoint by id from the database
        val loaded = database.gpsPointDao().get(id)

        // THEN - The loaded data contains the expected values
        assertNotNull(loaded as LocalGpsPoint)
        assertEquals(id, loaded.id)
        assertEquals(gpsPoint.stageId, loaded.stageId)
        assertEquals(gpsPoint.location, loaded.location)
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