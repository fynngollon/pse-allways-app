package com.pseteamtwo.allways.data.trip.source.local

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

    /**
     * Provides the [TripDao] for this database to modify the trips table.
     *
     * @return The [TripDao] for this database.
     */
    abstract fun tripDao(): TripDao

    /**
     * Provides the [StageDao] for this database to modify the stages table.
     *
     * @return The [StageDao] for this database.
     */
    abstract fun stageDao(): StageDao

    /**
     * Provides the [GpsPointDao] for this database to modify the gpsPoints table.
     *
     * @return The [GpsPointDao] for this database.
     */
    abstract fun gpsPointDao(): GpsPointDao
}