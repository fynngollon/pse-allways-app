package com.pseteamtwo.allways.account.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The Room Database that contains the Account table.
 */
//TODO("maybe exportSchema has to be true")
@Database(entities = [LocalAccount::class], version = 1, exportSchema = true)
abstract class AccountDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
}