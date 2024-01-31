package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import kotlin.jvm.Throws

interface QuestionnaireNetworkDataSource {

    @Throws(ServerConnectionFailedException::class)
    suspend fun loadQuestionnaire(): List<NetworkQuestion>

    private fun convertJsonToQuestions(jsonQuestionnaire: String): List<NetworkQuestion> {
        TODO("Not yet implemented")
    }
}