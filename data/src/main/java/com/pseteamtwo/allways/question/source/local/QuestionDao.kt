package com.pseteamtwo.allways.question.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun observeAll(): Flow<List<LocalQuestion>>

    @Upsert
    suspend fun upsertAll(localQuestions: List<LocalQuestion>)

    @Upsert
    suspend fun upsert(localQuestion: LocalQuestion)

    @Query("DELETE FROM questions WHERE id = :questionId")
    suspend fun deleteQuestion(questionId: String): Int
}