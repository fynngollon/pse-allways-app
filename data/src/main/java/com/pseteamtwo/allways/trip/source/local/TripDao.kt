package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun observeAll(): Flow<List<LocalTrip>>

    @Transaction
    @Query("SELECT * FROM trips")
    fun observeAllTripsWithStages(): Flow<List<LocalTripWithStages>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observe(tripId: Long): Flow<LocalTrip>

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observeTripWithStages(tripId: Long): Flow<LocalTripWithStages>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun get(tripId: Long): LocalTrip?

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripWithStages(tripId: Long): LocalTripWithStages?

    @Upsert
    suspend fun upsertAll(trips: List<LocalTrip>)

    @Insert
    suspend fun insert(trip: LocalTrip): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(trip: LocalTrip)

    @Query("UPDATE trips SET isConfirmed = :isConfirmed WHERE id = :tripId")
    suspend fun updateConfirmed(tripId: Long, isConfirmed: Boolean)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun delete(tripId: Long): Int

}