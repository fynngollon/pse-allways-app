package com.pseteamtwo.allways.question.source.network

import kotlinx.coroutines.sync.Mutex

class ProfileQuestionnaireNetworkDataSource : QuestionnaireNetworkDataSource {
    private val accessMutex = Mutex()

    override suspend fun loadQuestionnaire(): List<NetworkQuestion> {
        TODO("Not yet implemented")
    }
}