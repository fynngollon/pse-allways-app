package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
internal interface GpsPointDao {
    // TODO actually needs to be StateFlow!
    @Query("SELECT * FROM gps_points")
    fun observeAll(): Flow<List<LocalGpsPoint>>

    //@Query("SELECT * FROM gps_points WHERE id = :gpsPointId")
    //fun observe(gpsPointId: Long): Flow<LocalGpsPoint>

    @Query("SELECT * FROM gps_points WHERE id = :gpsPointId")
    suspend fun get(gpsPointId: Long): LocalGpsPoint?

    @Upsert
    suspend fun upsertAll(gpsPoints: List<LocalGpsPoint>)

    @Upsert
    suspend fun insert(gpsPoint: LocalGpsPoint): Long

    @Update
    suspend fun update(gpsPoint: LocalGpsPoint)

    @Query("DELETE FROM gps_points WHERE id = :gpsPointId")
    suspend fun delete(gpsPointId: Long): Int
}