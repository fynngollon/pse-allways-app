package com.pseteamtwo.allways.account.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * This data access object provides functionality to access the [AccountDatabase]
 * in terms of the [LocalAccount]s tables.
 *
 * According to [androidx.room], room will create an implementation of this interface to provide
 * the functionality of saving to, updating and extracting from the according
 * [androidx.room.Database]s.
 *
 * This functionality is defined for each method in this class by the according
 * [androidx.room] annotations (especially by [androidx.room.Query] commands).
 */
@Dao
interface AccountDao {
    /**
     * Observes the account saved in the database as it should be the only one.
     *
     * @return A flow of the requested account, if present (if not, flow will contain null).
     */
    @Query("SELECT * FROM account")
    fun observe(): Flow<LocalAccount>

    /**
     * Inserts or updates the account in the database.
     * (Inserts if not in the database; else updates).
     *
     * @param localAccount The account to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(localAccount: LocalAccount)

    /**
     * Deletes the account out of the database.
     *
     * @return How many accounts have been deleted.
     */
    @Query("DELETE FROM account")
    suspend fun deleteAccount(): Int
}