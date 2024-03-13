package com.pseteamtwo.allways.question.repository

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
import com.pseteamtwo.allways.question.toExternal
import com.pseteamtwo.allways.question.toLocal
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.lang.IllegalArgumentException

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

    private val email = "killua.zoldyck@hxh.com"
    private val password = "Godspeed!99"


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

    //this test can only succeed if there is a according questionnaire
    //(with a JSON only containing [unansweredQuestion1] and [unansweredQuestion2])
    //in the network database already
    @Test
    fun loadQuestionnaire() = runTest {
        repository.loadQuestionnaire()

        val questionsInDatabase = repository.observeAll().first()
        assertEquals(2, questionsInDatabase.size)
        assertEquals(
            listOf(unansweredQuestion1, unansweredQuestion2).toLocal().toExternal(),
            questionsInDatabase
        )
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
    fun deleteQuestion() = runTest {
        //Requires at least one question in the local database
        questionDao.upsertAll(listOf(unansweredQuestion1, unansweredQuestion2).toLocal())

        repository.deleteQuestion(unansweredQuestion1.id)

        val questionsInDatabase = repository.observeAll().first()
        assertEquals(1, questionsInDatabase.size)
        assertEquals(unansweredQuestion2.toLocal().toExternal(), questionsInDatabase.first())
    }

    @Test(expected = NullPointerException::class)
    fun refreshWithoutAnyAccountCurrentlyLoggedIn() = runTest {
        repository.refresh()
    }

    @Test
    fun refreshWithNoQuestionsForThisAccountOnServer() = runTest {
        accountRepository.createAccount(email, password)
        repository.refresh()

        val questionsInDatabase = repository.observeAll().first()
        assertEquals(0, questionsInDatabase.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun saveQuestionsWithoutAnswersToNetwork() = runTest {
        //Requires at least one question in the local database
        questionDao.upsertAll(listOf(unansweredQuestion1, unansweredQuestion2).toLocal())

        repository.saveQuestionsToNetwork(listOf(unansweredQuestion1.id, unansweredQuestion2.id))
    }

    @Test
    fun refreshQuestionsInLocalDatabaseAfterSavingThemToServerAndModifyingThem()
    = runTest {
        //Requires at least one question in the local database and all questions have a valid answer
        questionDao.upsertAll(listOf(answeredQuestion1, answeredQuestion2))
        accountRepository.createAccount(email, password)

        //Save questions to network database with current answers
        repository.saveQuestionsToNetwork(listOf(answeredQuestion1.id, answeredQuestion2.id))

        //Modify answers to another value locally
        repository.updateAnswer(answeredQuestion1.id, "24")
        repository.updateAnswer(answeredQuestion2.id, "Bachelor")

        //Use refresh method to restore former values still saved in network database
        repository.refresh()

        //Ensure that everything worked as intended and the former answers got restored
        val questionsInDatabase = repository.observeAll().first()
        assertEquals(2, questionsInDatabase.size)
        assertEquals(answeredQuestion1.toExternal(), questionsInDatabase[0])
        assertEquals(answeredQuestion2.toExternal(), questionsInDatabase[1])
    }
}