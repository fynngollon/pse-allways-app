package com.pseteamtwo.allways.data.question.source.network


import com.pseteamtwo.allways.data.question.QuestionType
import com.pseteamtwo.allways.data.question.source.network.NetworkQuestion
import com.pseteamtwo.allways.data.question.source.network.ProfileQuestionNetworkDataSource
import com.pseteamtwo.allways.data.question.source.network.QuestionNetworkDataSource
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
            "richyrich"
        )
        questions = listOf(networkQuestion1, networkQuestion2)
    }
    @Test
    fun loadQuestions() {
        runBlocking {
            try {
               profileQuestionNetworkDataSource.loadQuestions("richyrich")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun saveQuestions() {
        runBlocking {
            try {
                profileQuestionNetworkDataSource.saveQuestions("richyrich", questions)
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }

    @Test
    fun deleteQuestion() {
        runBlocking {
            try {
                profileQuestionNetworkDataSource.deleteQuestion("richyrich", "lol")
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}