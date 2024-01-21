package com.pseteamtwo.allways.question.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the Account table.
 */
//TODO("maybe exportSchema has to be false")
@Database(entities = [LocalQuestion::class], version = 1, exportSchema = false)
abstract class ProfileQuestionDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
}