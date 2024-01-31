package com.pseteamtwo.allways.trip.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the Stages table.
 */
//TODO("maybe exportSchema has to be true")
@Database(entities = [LocalStage::class], version = 1, exportSchema = false)
abstract class StageDatabase : RoomDatabase() {

    abstract fun stageDao(): StageDao
}