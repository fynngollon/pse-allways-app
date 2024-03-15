package com.pseteamtwo.allways.data.question.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the ProfileQuestions table.
 */
@Database(entities = [LocalQuestion::class], version = 1, exportSchema = false)
abstract class ProfileQuestionDatabase : RoomDatabase() {

    /**
     * Provides the [ProfileQuestionDao] for this database to modify
     * the ProfileQuestions table.
     *
     * @return The [ProfileQuestionDao] for this database.
     */
    abstract fun profileQuestionDao(): ProfileQuestionDao
}