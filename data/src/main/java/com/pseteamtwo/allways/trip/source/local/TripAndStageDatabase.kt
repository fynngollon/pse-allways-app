package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the Trips table.
 */
@Database(
    entities = [LocalTrip::class, LocalStage::class, LocalGpsPoint::class],
    version = 1,
    exportSchema = false
)
abstract class TripAndStageDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun stageDao(): StageDao
    abstract fun gpsPointDao(): GpsPointDao
}