package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.account.repository.AccountRepository
import com.pseteamtwo.allways.di.ApplicationScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class DefaultQuestionRepository<T: QuestionDao,
        S: QuestionNetworkDataSource, U: QuestionnaireNetworkDataSource>(
    protected val questionDao:  T,
    protected val questionNetworkDataSource:  S,
    protected val questionnaireNetworkDataSource: U,
    protected val accountRepository: AccountRepository,
    @DefaultDispatcher protected val dispatcher: CoroutineDispatcher,
    @ApplicationScope protected val scope: CoroutineScope,
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
        val question = questionDao.observe(id).first()
        //update answer from question and upsert it
        question.answer = answer
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

        //gets all Questions from local Database ans saves them in the list. Ids which done exist get ignored
        for (id in idList) {
            questions.add(questionDao.observe(id).first())
        }

        val pseudonym = withContext(dispatcher) {
            accountRepository.observe().first().pseudonym
        }
        //saves all Questions to Network
        questionNetworkDataSource.saveQuestions(accountRepository.observe().first().pseudonym, questions.toNetwork(pseudonym))
    }
}

/*fun questions(): List<Question> {
    var question1: Question = Question(
        id = "1",
        title = "Anzahl Haustiere",
        type = QuestionType.SPINNER,
        options = listOf("option1", "option2"),
        answer = "test"
    )
    var question2: Question = Question(
        id = "2",
        title = "Lieblings Eissorte",
        type = QuestionType.TEXT,
        options = listOf("option1", "option2"),
        answer = "test"
    )
    var question3: Question = Question(
        id = "3",
        title = "question3",
        type = QuestionType.CHECKBOX,
        options = listOf("option1", "option2"),
        answer = "test"
    )

    return listOf(question1, question2, question3)
}*/




