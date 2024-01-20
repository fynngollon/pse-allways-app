package com.pseteamtwo.allways.question.source.network

import kotlinx.coroutines.sync.Mutex

class HouseholdQuestionNetworkDataSource : QuestionNetworkDataSource {
    private val accessMutex = Mutex()

    override suspend fun loadQuestions(pseudonym: String): List<NetworkQuestion> {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuestions(questions: List<NetworkQuestion>) {
        TODO("Not yet implemented")
    }
}