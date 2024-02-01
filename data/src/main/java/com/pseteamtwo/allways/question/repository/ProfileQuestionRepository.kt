package com.pseteamtwo.allways.question.repository


import com.pseteamtwo.allways.di.ApplicationScope
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.QuestionType
import com.pseteamtwo.allways.question.source.local.LocalQuestion
import com.pseteamtwo.allways.question.source.local.QuestionDao
import com.pseteamtwo.allways.question.source.network.QuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.QuestionnaireNetworkDataSource
import com.pseteamtwo.allways.question.toExternal
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
    private val profileQuestionDatabase:  QuestionDao,
    private val profileQuestionNetworkDataSource:  QuestionNetworkDataSource,
    private val profileQuestionnaireNetworkDataSource: QuestionnaireNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): QuestionRepository {


    override fun observeAll(): Flow<List<Question>> {
        return profileQuestionDatabase.observeAll().map { it.toExternal() }
    }

    override suspend fun loadQuestionnaire() {
        //loads all Questions from the Json Data and saves them locally
        TODO("Not yet implemented")
    }

    override suspend fun updateAnswer(id: String, answer: String) {
        val question = profileQuestionDatabase.observe(id).first()
        //update answer from question and upsert it
        question.answer = answer
        profileQuestionDatabase.upsert(question)
    }

    override suspend fun deleteQuestion(id: String) {
        profileQuestionDatabase.deleteQuestion(id)
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuestionsToNetwork(idList: List<String>) {
        val questions = mutableListOf<LocalQuestion>()

        //gets all Questions from local Database ans saves them in the list. Ids which done exist get ignored
        for (id in idList) {
            questions.add(profileQuestionDatabase.observe(id).first())
        }

        //saves all Questions to Network
        profileQuestionNetworkDataSource.saveQuestions(questions.toNetwork())
    }
}