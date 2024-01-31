package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StageDao {
    @Query("SELECT * FROM stages WHERE tripId = :tripId") //TODO does it give all trips
    fun getStagesForTrip(tripId: String): Flow<List<LocalStage>>

    @Upsert
    suspend fun upsertAll(stages: List<LocalStage>)

    @Upsert
    suspend fun upsert(stage: LocalStage)

    @Query("DELETE FROM stages WHERE id = :stageId")
    suspend fun delete(stageId: String): Int
}