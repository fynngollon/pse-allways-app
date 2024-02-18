package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pseteamtwo.allways.typeconverter.ListOfLocalGpsPointConverter

/**
 * The Room Database that contains the Stages table.
 */
@Database(entities = [LocalStage::class, LocalGpsPoint::class, LocalTrip::class], version = 1, exportSchema = false)
@TypeConverters(ListOfLocalGpsPointConverter::class)
abstract class StageDatabase : RoomDatabase() {

    abstract fun stageDao(): StageDao
}