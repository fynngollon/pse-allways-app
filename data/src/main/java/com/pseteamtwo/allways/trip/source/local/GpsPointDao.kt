package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface GpsPointDao {
    @Query("SELECT * FROM gpsPoints")
    fun observeAll(): StateFlow<List<LocalGpsPoint>>

    @Query("SELECT * FROM gpsPoints WHERE id = :gpsPointId")
    fun observe(gpsPointId: String): Flow<LocalGpsPoint>

    @Upsert
    suspend fun upsertAll(gpsPoints: List<LocalGpsPoint>)

    @Upsert
    suspend fun upsert(gpsPoint: LocalGpsPoint)

    @Query("DELETE FROM gpsPoints WHERE id = :gpsPointId")
    suspend fun delete(gpsPointId: String): Int
}