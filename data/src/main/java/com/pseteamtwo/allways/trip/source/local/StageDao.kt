package com.pseteamtwo.allways.trip.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StageDao {
    @Query("") //TODO
    fun getStagesForTrip(tripId: String): Flow<List<LocalStage>>

    @Upsert //TODO
    suspend fun upsertAll(stages: List<LocalStage>)

    @Upsert
    suspend fun upsert(stage: LocalStage)

    @Query("") //TODO
    suspend fun delete(stageId: String): Int
}