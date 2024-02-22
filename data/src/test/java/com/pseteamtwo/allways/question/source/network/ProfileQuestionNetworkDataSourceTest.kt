package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.question.QuestionType
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.sql.SQLException

class ProfileQuestionNetworkDataSourceTest {

    private lateinit var profileQuestionNetworkDataSource: QuestionNetworkDataSource
    private lateinit var networkQuestion: NetworkQuestion

    @Before
    fun setUp() {
        profileQuestionNetworkDataSource = ProfileQuestionNetworkDataSource()
        networkQuestion = NetworkQuestion(
            "lol",
            "Bist du dumm?",
            QuestionType.TEXT,
        )
    }
    @Test
    fun loadQuestions() {
        runBlocking {
            try {
               profileQuestionNetworkDataSource.loadQuestions("kdb")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun saveQuestions() {
    }

    @Test
    fun deleteQuestion() {
        runBlocking {
            try {
                profileQuestionNetworkDataSource.deleteQuestion("kdb", "lol")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}