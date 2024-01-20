package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface QuestionNetworkDataSource {
    @Throws(ServerConnectionFailedException::class)
    suspend fun loadQuestions(pseudonym: String): List<NetworkQuestion>

    @Throws(ServerConnectionFailedException::class)
    suspend fun saveQuestions(questions: List<NetworkQuestion>)

    //TODO("maybe delete function")
}