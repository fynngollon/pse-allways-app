package com.pseteamtwo.allways.account.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM account")
    fun observe(): Flow<LocalAccount>

    @Upsert
    suspend fun upsert(localAccount: LocalAccount)

    @Query("DELETE FROM account")
    suspend fun deleteAccount()
}