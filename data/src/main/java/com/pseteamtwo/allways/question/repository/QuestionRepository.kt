package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.exception.QuestionIdNotFoundException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.QuestionType
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

interface QuestionRepository {
    fun observeAll(): Flow<List<Question>>

    suspend fun createQuestion(
        id: String,
        title: String,
        type: QuestionType,
        options: List<String>
    ): String

    @Throws(QuestionIdNotFoundException::class)
    suspend fun updateAnswer(id: String, answer: String)

    @Throws(QuestionIdNotFoundException::class)
    suspend fun deleteQuestion(id: String)

    @Throws(ServerConnectionFailedException::class)
    suspend fun refresh()

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveQuestionsToNetwork(id: List<String>)
}