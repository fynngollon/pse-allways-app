package com.pseteamtwo.allways.account.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Representation
 *
 * @constructor Create empty Account dao
 */
@Dao
interface AccountDao {
    /**
     * Observe
     *
     * @return
     */
    @Query("SELECT * FROM account")
    fun observe(): Flow<LocalAccount>

    /**
     * Upsert
     *
     * @param localAccount
     */
    @Upsert
    suspend fun upsert(localAccount: LocalAccount)

    /**
     * Delete account
     *
     * @return
     */
    @Query("DELETE FROM account")
    suspend fun deleteAccount(): Int
}