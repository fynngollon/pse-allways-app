package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.QuestionType
import kotlinx.coroutines.flow.Flow

class HouseholdQuestionRepository : QuestionRepository {
    override fun observeAll(): Flow<List<Question>> {
        TODO("Not yet implemented")
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