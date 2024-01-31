package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("") //TODO
    fun observeAll(): Flow<List<LocalTrip>>

    @Query("") //TODO
    fun observe(): Flow<LocalTrip>

    @Upsert //TODO
    suspend fun upsertAll(trips: List<LocalTrip>)

    @Upsert
    suspend fun upsert(trip: LocalTrip)

    @Query("") //TODO
    suspend fun updateConfirmed(tripId: String, isConfirmed: Boolean)

    @Query("") //TODO
    suspend fun delete(tripId: String): Int
}