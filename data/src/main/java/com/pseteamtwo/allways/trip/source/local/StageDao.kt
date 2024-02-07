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
interface StageDao {

    @Transaction
    @Query("SELECT * FROM stages WHERE tripId = :tripId") //TODO does it give all trips
    fun getStagesForTrip(tripId: Long): Flow<List<LocalStage>>

    @Query("SELECT * FROM stages WHERE id = :stageId")
    suspend fun get(stageId: Long): LocalStage?

    @Query("SELECT * FROM stages")
    suspend fun getAll(): List<LocalStage>

    @Upsert
    suspend fun upsertAll(stages: List<LocalStage>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(stage: LocalStageWithoutGpsPoints): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(stage: LocalStage)

    @Query("DELETE FROM stages WHERE id = :stageId")
    suspend fun delete(stageId: Long): Int

    //@Transaction
    //@Query("SELECT * FROM stages WHERE :startTime < (SELECT MAX(gpsPoints.) FROM gps_points WHERE stageId = stages.id) AND :checkEndTime > (SELECT MIN(location.time) FROM gps_points WHERE stageId = stages.id)")
    //suspend fun getConflictingStages(startTime: Long, endTime: Long)
}