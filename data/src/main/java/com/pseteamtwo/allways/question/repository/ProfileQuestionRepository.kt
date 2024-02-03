package com.pseteamtwo.allways.question.repository


import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.source.local.LocalQuestion
import com.pseteamtwo.allways.question.source.local.QuestionDao
import com.pseteamtwo.allways.question.source.network.QuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.QuestionnaireNetworkDataSource
import com.pseteamtwo.allways.question.toExternal
import com.pseteamtwo.allways.question.toLocal
import com.pseteamtwo.allways.question.toNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileQuestionRepository @Inject constructor(
    private val profileQuestionDao:  QuestionDao,
    private val profileQuestionNetworkDataSource:  QuestionNetworkDataSource,
    private val profileQuestionnaireNetworkDataSource: QuestionnaireNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): QuestionRepository {


    override fun observeAll(): Flow<List<Question>> {
        return profileQuestionDao.observeAll().map { it.toExternal() }
    }

    override suspend fun loadQuestionnaire() {
        val networkQuestions = profileQuestionnaireNetworkDataSource.loadQuestionnaire()
        profileQuestionDao.upsertAll(networkQuestions.toLocal())
    }

    override suspend fun updateAnswer(id: String, answer: String) {
        val question = profileQuestionDao.observe(id).first()
        //update answer from question and upsert it
        question.answer = answer
        profileQuestionDao.upsert(question)
    }

    override suspend fun deleteQuestion(id: String) {
        profileQuestionDao.deleteQuestion(id)
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuestionsToNetwork(idList: List<String>) {
        val questions = mutableListOf<LocalQuestion>()

        //gets all Questions from local Database ans saves them in the list. Ids which done exist get ignored
        for (id in idList) {
            questions.add(profileQuestionDao.observe(id).first())
        }

        //saves all Questions to Network
        profileQuestionNetworkDataSource.saveQuestions(questions.toNetwork())
    }
}