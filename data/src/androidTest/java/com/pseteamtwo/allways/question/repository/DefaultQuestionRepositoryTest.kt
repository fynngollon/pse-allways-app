package com.pseteamtwo.allways.question.repository

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pseteamtwo.allways.account.repository.DefaultAccountRepository
import com.pseteamtwo.allways.account.source.local.AccountDao
import com.pseteamtwo.allways.account.source.local.AccountDatabase
import com.pseteamtwo.allways.account.source.network.AccountNetworkDataSource
import com.pseteamtwo.allways.account.source.network.DefaultAccountNetworkDataSource
import com.pseteamtwo.allways.question.QuestionType
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDao
import com.pseteamtwo.allways.question.source.local.ProfileQuestionDatabase
import com.pseteamtwo.allways.question.source.network.NetworkQuestion
import com.pseteamtwo.allways.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.question.source.network.ProfileQuestionnaireNetworkDataSource
import com.pseteamtwo.allways.question.toLocal
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 * These tests require an established connection to the network database in order to ensure right
 * testing because otherwise all tests will likely fail
 * because of [com.pseteamtwo.allways.exception.ServerConnectionFailedException].
 *
 * This test tests [com.pseteamtwo.allways.question.repository.DefaultQuestionRepository]
 * while testing it regarding only
 * [com.pseteamtwo.allways.question.repository.ProfileQuestionRepository]
 * because [com.pseteamtwo.allways.question.repository.HouseholdQuestionRepository]
 * works just alike.
 */
class DefaultQuestionRepositoryTest {

    //Test data
    private val unansweredQuestion1 = NetworkQuestion(
        "age",
        "age",
        QuestionType.TEXT
    )
    private val answeredQuestion1 = unansweredQuestion1.copy(answer = "21").toLocal()

    private val unansweredQuestion2 = NetworkQuestion(
        "education",
        "highest degree of education",
        QuestionType.CHECKBOX,
        listOf("Abitur", "Bachelor", "Master")
    )
    private val answeredQuestion2 = unansweredQuestion2.copy(answer = "Abitur").toLocal()

    private val format = Json {
        ignoreUnknownKeys = true // Add this line to ignore unknown keys if needed
    }
    private val unansweredQuestionString: String = format.encodeToString(
        listOf(unansweredQuestion1, unansweredQuestion2)
    )


    //Test dependencies
    private lateinit var questionDatabase: ProfileQuestionDatabase
    private lateinit var questionDao: ProfileQuestionDao
    private lateinit var questionNetworkDataSource: ProfileQuestionNetworkDataSource
    private lateinit var questionnaireNetworkDataSource: ProfileQuestionnaireNetworkDataSource

    private lateinit var accountDatabase: AccountDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var accountNetworkDataSource: AccountNetworkDataSource
    private lateinit var accountRepository: DefaultAccountRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    //Class under test
    private lateinit var repository: ProfileQuestionRepository


    // using an in-memory database because the information stored here disappears when the
    // process is killed
    // Ensure that we use a new database for each test.
    @Before
    fun createRepository() {
        questionDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProfileQuestionDatabase::class.java
        ).allowMainThreadQueries().build()

        accountDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AccountDatabase::class.java
        ).allowMainThreadQueries().build()

        questionDao = questionDatabase.profileQuestionDao()
        questionNetworkDataSource = ProfileQuestionNetworkDataSource()
        questionnaireNetworkDataSource = ProfileQuestionnaireNetworkDataSource()
        accountDao = accountDatabase.accountDao()
        accountNetworkDataSource = DefaultAccountNetworkDataSource()

        accountRepository = DefaultAccountRepository(
            accountDao,
            accountNetworkDataSource,
            testDispatcher,
            //testScope
        )

        repository = ProfileQuestionRepository(
            questionDao,
            questionNetworkDataSource,
            questionnaireNetworkDataSource,
            accountRepository,
            testDispatcher,
            //testScope
        )
    }


    @Test
    fun loadQuestionnaire() {
        Log.i("QuestionsJSON", unansweredQuestionString)
    }

    @Test
    fun updateAnswerAndGetBack() = runTest {
        //Requires at least one question in the local database
        questionDao.upsertAll(listOf(unansweredQuestion1, unansweredQuestion2).toLocal())

        repository.updateAnswer(unansweredQuestion1.id, answeredQuestion1.answer)
        repository.updateAnswer(unansweredQuestion2.id, answeredQuestion2.answer)
        val updatedQuestion1 = questionDao.get(unansweredQuestion1.id)
        val updatedQuestion2 = questionDao.get(unansweredQuestion2.id)

        assertEquals(answeredQuestion1, updatedQuestion1)
        assertEquals(answeredQuestion2, updatedQuestion2)
    }

    @Test
    fun deleteQuestion() {
        TODO("Not yet implemented")
    }

    @Test
    fun refresh() {
        TODO("Not yet implemented")
    }

    @Test
    fun saveQuestionsToNetwork() {
        TODO("Not yet implemented")
    }
}