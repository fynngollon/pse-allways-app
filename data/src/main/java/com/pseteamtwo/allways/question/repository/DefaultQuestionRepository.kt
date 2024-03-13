package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.DefaultDispatcher
import com.pseteamtwo.allways.exception.QuestionIdNotFoundException
import com.pseteamtwo.allways.exception.ServerConnectionFailedException
import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.source.local.LocalQuestion
import com.pseteamtwo.allways.question.source.local.QuestionDao
import com.pseteamtwo.allways.question.source.network.QuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.QuestionnaireNetworkDataSource
import com.pseteamtwo.allways.question.toExternal
import com.pseteamtwo.allways.question.toLocal
import com.pseteamtwo.allways.question.toNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Default question repository
 *
 * @param T Type of [questionDao] needs to be inherited from [QuestionDao].
 * @param S Type of [questionNetworkDataSource] needs to be inherited
 * from [QuestionNetworkDataSource].
 * @param U Type of [questionnaireNetworkDataSource] needs to be inherited
 * from [QuestionnaireNetworkDataSource].
 * @property questionDao To access a local question database.
 * @property questionNetworkDataSource To access a network question database.
 * @property questionnaireNetworkDataSource To access a network database providing all
 * questions referring to type [U] information of the user.
 * @property accountRepository To access the user's account data for saving
 * and retrieving data from the network database.
 * @property dispatcher A dispatcher to allow asynchronous function calls because this class uses
 * complex computing and many accesses to databases which shall not block the program flow.
 * @constructor Creates an instance of this class.
 */
abstract class DefaultQuestionRepository<T: QuestionDao,
        S: QuestionNetworkDataSource, U: QuestionnaireNetworkDataSource>(
    protected val questionDao:  T,
    protected val questionNetworkDataSource:  S,
    protected val questionnaireNetworkDataSource: U,
    protected val accountRepository: AccountRepository,
    @DefaultDispatcher protected val dispatcher: CoroutineDispatcher,
    //@ApplicationScope protected val scope: CoroutineScope
) : QuestionRepository {



    override fun observeAll(): Flow<List<Question>>  {
        return questionDao.observeAll().map { it.toExternal() }
    }

    @Throws(ServerConnectionFailedException::class)
    override suspend fun loadQuestionnaire() {
        val networkQuestions = questionnaireNetworkDataSource.loadQuestionnaire()
        questionDao.upsertAll(networkQuestions.toLocal())
    }

    @Throws(QuestionIdNotFoundException::class)
    override suspend fun updateAnswer(id: String, answer: String) {
        val question = questionDao.get(id)
        //update answer from question and upsert it
        question!!.answer = answer
        questionDao.upsert(question)
        //questions[id.toInt()].answer = answer
    }


    @Throws(QuestionIdNotFoundException::class)
    override suspend fun deleteQuestion(id: String) {
        questionDao.deleteQuestion(id)
    }

    @Throws(ServerConnectionFailedException::class)
    override suspend fun refresh() {
        val pseudonym = withContext(dispatcher) {
            accountRepository.observe().first().pseudonym
        }
        val networkQuestions = questionNetworkDataSource.loadQuestions(pseudonym)
        if(networkQuestions.isEmpty()) {
            return
        }

        val localQuestions = networkQuestions.toLocal()
        questionDao.deleteAll()
        questionDao.upsertAll(localQuestions)
    }

    @Throws(ServerConnectionFailedException::class)
    override suspend fun saveQuestionsToNetwork(idList: List<String>) {
        val questions = mutableListOf<LocalQuestion>()

        //gets all Questions from local Database ans saves them in the list. Ids which don't
        //exist get ignored
        for (id in idList) {
            questions.add(questionDao.observe(id).first())
        }

        val pseudonym = withContext(dispatcher) {
            accountRepository.observe().first().pseudonym
        }
        //saves all Questions to Network
        questionNetworkDataSource.saveQuestions(accountRepository.observe().first().pseudonym,
            questions.toNetwork(pseudonym))
    }
}