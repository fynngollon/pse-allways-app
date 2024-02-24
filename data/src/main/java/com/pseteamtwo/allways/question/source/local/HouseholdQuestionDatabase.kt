package com.pseteamtwo.allways.question.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the HouseholdQuestions table.
 */
@Database(entities = [LocalQuestion::class], version = 1, exportSchema = false)
abstract class HouseholdQuestionDatabase : RoomDatabase() {

    /**
     * Provides the [HouseholdQuestionDao] for this database to modify
     * the HouseholdQuestions table.
     *
     * @return The [HouseholdQuestionDao] for this database.
     */
    abstract fun householdQuestionDao(): HouseholdQuestionDao
}