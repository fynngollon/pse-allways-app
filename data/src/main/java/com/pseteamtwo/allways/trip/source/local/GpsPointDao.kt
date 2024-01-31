package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface GpsPointDao {
    @Query("") //TODO
    fun observeAll(): StateFlow<List<LocalGpsPoint>>

    @Query("") //TODO
    fun observe(): Flow<LocalGpsPoint>

    @Upsert //TODO
    suspend fun upsertAll(gpsPoints: List<LocalGpsPoint>)

    @Upsert
    suspend fun upsert(gpsPoint: LocalGpsPoint)

    @Query("") //TODO
    suspend fun delete(gpsPointId: String): Int
}