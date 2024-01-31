package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun observeAll(): Flow<List<LocalTrip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observe(tripId: String): Flow<LocalTrip>

    @Upsert
    suspend fun upsertAll(trips: List<LocalTrip>)

    @Upsert
    suspend fun upsert(trip: LocalTrip)

    @Query("UPDATE trips SET isConfirmed = :isConfirmed WHERE id = :tripId")
    suspend fun updateConfirmed(tripId: String, isConfirmed: Boolean)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun delete(tripId: String): Int
}