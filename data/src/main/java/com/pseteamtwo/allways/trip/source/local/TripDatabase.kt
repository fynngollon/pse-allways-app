package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pseteamtwo.allways.typeconverter.ListOfLocalStageConverter

/**
 * The Room Database that contains the Trips table.
 */
@Database(entities = [LocalTrip::class, LocalStage::class, LocalGpsPoint::class], version = 1, exportSchema = false)
@TypeConverters(ListOfLocalStageConverter::class)
abstract class TripDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
}