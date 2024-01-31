package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the Trips table.
 */
//TODO("maybe exportSchema has to be true")
@Database(entities = [LocalTrip::class], version = 1, exportSchema = false)
abstract class TripDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
}