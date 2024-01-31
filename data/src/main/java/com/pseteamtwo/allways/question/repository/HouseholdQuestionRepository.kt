package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.QuestionType
import com.pseteamtwo.allways.question.source.local.QuestionDao
import com.pseteamtwo.allways.question.source.network.QuestionNetworkDataSource
import com.pseteamtwo.allways.question.toExternal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HouseholdQuestionRepository @Inject constructor(
    private val householdQuestionDatabase: QuestionDao,
    private val householdQuestionNetworkDataSource: QuestionNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): QuestionRepository {

    override fun observeAll(): Flow<List<Question>> {
        return householdQuestionDatabase.observeAll().map { it.toExternal() }
    }

    override suspend fun createQuestion(
        id: String,
        title: String,
        type: QuestionType,
        options: List<String>
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun updateAnswer(id: String, answer: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuestion(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuestionsToNetwork(id: List<String>) {
        TODO("Not yet implemented")
    }
}