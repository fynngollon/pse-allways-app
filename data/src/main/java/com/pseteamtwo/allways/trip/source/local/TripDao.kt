package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun observeAll(): Flow<List<LocalTrip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observe(tripId: Long): Flow<LocalTrip>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun get(tripId: Long): LocalTrip?

    @Upsert
    suspend fun upsertAll(trips: List<LocalTrip>)

    @Insert
    suspend fun insert(trip: LocalTripWithoutStages): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(trip: LocalTrip)

    @Query("UPDATE trips SET isConfirmed = :isConfirmed WHERE id = :tripId")
    suspend fun updateConfirmed(tripId: Long, isConfirmed: Boolean)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun delete(tripId: Long): Int
}