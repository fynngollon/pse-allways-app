package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the GpsPoints table.
 */
//TODO("maybe exportSchema has to be true")
@Database(entities = [LocalGpsPoint::class], version = 1, exportSchema = false)
abstract class GpsPointDatabase : RoomDatabase() {

    abstract fun gpsPointDao(): GpsPointDao
}