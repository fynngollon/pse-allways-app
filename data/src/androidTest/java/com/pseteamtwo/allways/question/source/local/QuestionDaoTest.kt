package com.pseteamtwo.allways.question.source.local
/*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.question.QuestionType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * This test tests [com.pseteamtwo.allways.question.source.local.ProfileQuestionDao]
 * and [com.pseteamtwo.allways.question.source.local.ProfileQuestionDatabase].
 *
 * [com.pseteamtwo.allways.question.source.local.HouseholdQuestionDao] and
 * [com.pseteamtwo.allways.question.source.local.HouseholdQuestionDatabase] have similar
 * functionality.
 */
@RunWith(AndroidJUnit4::class)
class QuestionDaoTest {
    // using an in-memory database because the information stored here disappears when the
    // process is killed
    private lateinit var database: ProfileQuestionDatabase


    // Ensure that we use a new database for each test.
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProfileQuestionDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertQuestionAndGetById() = runTest {
        // GIVEN - insert a task
        val question = LocalQuestion(
            "id",
            "title",
            QuestionType.TEXT,
            listOf("option_1", "option_2", "option_3"),
            "option_3",
        )
        database.profileQuestionDao().upsert(question)

        // WHEN - Get the task by id from the database
        val loaded = database.profileQuestionDao().observe(question.id).first()

        // THEN - The loaded data contains the expected values
        assertNotNull(loaded as LocalQuestion)
        assertEquals(question.id, loaded.id)
        assertEquals(question.title, loaded.title)
        assertEquals(question.type, loaded.type)
        assertEquals(question.options, loaded.options)
        assertEquals(question.answer, loaded.answer)
    }

    @Test
    fun insertQuestionUpdateAnswerThenGetById() = runTest {
        // GIVEN - insert a task
        val question = LocalQuestion(
            "id",
            "title",
            QuestionType.TEXT,
            listOf("option_1", "option_2", "option_3"),
            "option_3",
        )
        database.profileQuestionDao().upsert(question)

        val newQuestion = LocalQuestion(
            "id",
            "title",
            QuestionType.TEXT,
            listOf("option_1", "option_2", "option_3"),
            "option_1",
        )
        database.profileQuestionDao().upsert(newQuestion)

        // WHEN - Get the question by id from the database
        val loaded = database.profileQuestionDao().observe(question.id).first()

        // THEN - The loaded data contains the expected values
        assertNotNull(loaded as LocalQuestion)
        assertEquals(question.id, loaded.id)
        assertEquals(question.title, loaded.title)
        assertEquals(question.type, loaded.type)
        assertEquals(question.options, loaded.options)
        assertEquals(newQuestion.answer, loaded.answer)
    }

    @Test
    fun insertQuestionsDeleteOneThenGetAll() = runTest {
        // GIVEN - insert a question
        val question = LocalQuestion(
            "id",
            "title",
            QuestionType.TEXT,
            listOf("option_1", "option_2", "option_3"),
            "option_3",
        )

        val newQuestion = LocalQuestion(
            "id2",
            "newTitle",
            QuestionType.RADIO_BUTTON,
            listOf("option_3", "option_4", "option_5"),
            "option_5",
        )
        database.profileQuestionDao().upsertAll(listOf(question, newQuestion))

        val loaded = database.profileQuestionDao().observeAll().first()
        assertEquals(2, loaded.size)

        database.profileQuestionDao().deleteQuestion(question.id)

        // WHEN - Get the task by id from the database
        val newLoaded = database.profileQuestionDao().observeAll().first()
        assertEquals(1, newLoaded.size)
    }
}

 */