package com.pseteamtwo.allways.question.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("") //TODO
    fun observeAll(): Flow<List<LocalQuestion>>

    @Upsert //TODO
    suspend fun upsertAll(localQuestions: List<LocalQuestion>)

    @Upsert
    suspend fun upsert(localQuestion: LocalQuestion)

    @Query("") //TODO
    suspend fun deleteQuestion(id: String): Int
}