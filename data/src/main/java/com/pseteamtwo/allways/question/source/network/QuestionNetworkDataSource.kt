package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

internal interface QuestionNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadQuestions(pseudonym: String): List<NetworkQuestion>

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveQuestions(pseudonym: String, questions: List<NetworkQuestion>)

    suspend fun deleteQuestion(pseudonym: String, id: String)
}