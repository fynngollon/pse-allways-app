package com.pseteamtwo.allways.question.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the HouseholdQuestions table.
 */
@Database(entities = [LocalQuestion::class], version = 1, exportSchema = false)
abstract class HouseholdQuestionDatabase : RoomDatabase() {

    abstract fun householdQuestionDao(): HouseholdQuestionDao
}