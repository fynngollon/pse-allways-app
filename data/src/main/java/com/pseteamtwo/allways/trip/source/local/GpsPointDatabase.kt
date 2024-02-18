package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pseteamtwo.allways.typeconverter.LocationConverter

/**
 * The Room Database that contains the GpsPoints table.
 */
//TODO("maybe exportSchema has to be true")
@Database(entities = [LocalGpsPoint::class, LocalStage::class, LocalTrip::class], version = 1, exportSchema = true)
@TypeConverters(LocationConverter::class)
abstract class GpsPointDatabase : RoomDatabase() {

    abstract fun gpsPointDao(): GpsPointDao
}