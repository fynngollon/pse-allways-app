package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.question.QuestionType
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.sql.SQLException

class ProfileQuestionNetworkDataSourceTest {

    private lateinit var profileQuestionNetworkDataSource: QuestionNetworkDataSource
    private lateinit var networkQuestion1: NetworkQuestion
    private lateinit var networkQuestion2: NetworkQuestion
    private lateinit var questions: List<NetworkQuestion>

    @Before
    fun setUp() {
        profileQuestionNetworkDataSource = ProfileQuestionNetworkDataSource()
        networkQuestion1 = NetworkQuestion(
            "lol",
            "Bist du dumm?",
            QuestionType.TEXT,
        )
        networkQuestion2 = NetworkQuestion(
            "richy",
            "Bist du schlau?",
            QuestionType.TEXT,
            emptyList(),
            "Ja",
            "kdb"
        )
        questions = listOf(networkQuestion1, networkQuestion2)
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
        runBlocking {
            try {
                profileQuestionNetworkDataSource.saveQuestions("kdb", questions)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
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