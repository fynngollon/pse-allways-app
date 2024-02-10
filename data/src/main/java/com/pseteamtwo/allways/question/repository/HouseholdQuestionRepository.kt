package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HouseholdQuestionRepository @Inject constructor(
    private val householdQuestionDao:  QuestionDao,
    private val householdQuestionNetworkDataSource:  QuestionNetworkDataSource,
    private val householdQuestionnaireNetworkDataSource: QuestionnaireNetworkDataSource,
    private val accountRepository: AccountRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
): QuestionRepository {


    override fun observeAll(): Flow<List<Question>> {
        return householdQuestionDao.observeAll().map { it.toExternal() }
    }

    override suspend fun loadQuestionnaire() {
        val networkQuestions = householdQuestionnaireNetworkDataSource.loadQuestionnaire()
        householdQuestionDao.upsertAll(networkQuestions.toLocal())
    }

    override suspend fun updateAnswer(id: String, answer: String) {
        val question = householdQuestionDao.observe(id).first()
        //update answer from question and upsert it
        question.answer = answer
        householdQuestionDao.upsert(question)
    }

    override suspend fun deleteQuestion(id: String) {
        householdQuestionDao.deleteQuestion(id)
    }

    override suspend fun refresh() {
        val networkQuestions = householdQuestionnaireNetworkDataSource.loadQuestionnaire()
        val localQuestions = networkQuestions.toLocal()

        householdQuestionDao.deleteAll()
        householdQuestionDao.upsertAll(localQuestions)
    }

    override suspend fun saveQuestionsToNetwork(idList: List<String>) {
        val questions = mutableListOf<LocalQuestion>()

        //gets all Questions from local Database ans saves them in the list. Ids which done exist get ignored
        for (id in idList) {
            questions.add(householdQuestionDao.observe(id).first())
        }

        val pseudonym = withContext(dispatcher) {
            accountRepository.observe().first().pseudonym
        }
        //saves all Questions to Network
        householdQuestionNetworkDataSource.saveQuestions(questions.toNetwork(pseudonym))
    }
}