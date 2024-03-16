package com.pseteamtwo.allways.ui.profile

import com.pseteamtwo.allways.data.question.source.local.LocalQuestion
import com.pseteamtwo.allways.data.question.source.local.ProfileQuestionDao
import kotlinx.coroutines.flow.Flow

class FakeProfileQuestionDao : ProfileQuestionDao{
    override fun observeAll(): Flow<List<LocalQuestion>> {
        TODO("Not yet implemented")
    }

    override fun observe(questionId: String): Flow<LocalQuestion> {
        TODO("Not yet implemented")
    }

    override suspend fun get(questionId: String): LocalQuestion? {
        TODO("Not yet implemented")
    }

    override suspend fun upsertAll(localQuestions: List<LocalQuestion>) {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(localQuestion: LocalQuestion) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuestion(questionId: String): Int {
        TODO("Not yet implemented")
    }
}