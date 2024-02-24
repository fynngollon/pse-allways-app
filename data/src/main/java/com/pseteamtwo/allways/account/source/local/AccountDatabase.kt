package com.pseteamtwo.allways.account.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDao

/**
 * The Room database that contains the account table.
 *
 */
@Database(entities = [LocalAccount::class], version = 1, exportSchema = false)
abstract class AccountDatabase : RoomDatabase() {

    /**
     *  Provides the [AccountDao] for this database to modify the Account table.
     *
     * @return [AccountDao] for this table.
     */
    abstract fun accountDao(): AccountDao
}