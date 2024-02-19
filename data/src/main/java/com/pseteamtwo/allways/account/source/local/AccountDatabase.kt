package com.pseteamtwo.allways.account.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the Account table.
 */
@Database(entities = [LocalAccount::class], version = 1, exportSchema = false)
abstract class AccountDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
}