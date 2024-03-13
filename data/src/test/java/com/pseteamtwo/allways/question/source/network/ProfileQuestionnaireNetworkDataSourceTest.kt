package com.pseteamtwo.allways.question.source.network

/**
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.sql.SQLException

class ProfileQuestionnaireNetworkDataSourceTest {

    private lateinit var profileQuestionnaireNetworkDataSource: QuestionnaireNetworkDataSource
    @Before
    fun setUp() {
        profileQuestionnaireNetworkDataSource = ProfileQuestionnaireNetworkDataSource()
    }

    @Test
    fun loadQuestionnaire() {
        runBlocking {
            try {
                profileQuestionnaireNetworkDataSource.loadQuestionnaire()
            } catch (e: SQLException) {
                assert(false){ "SQL Exception" }
            }
        }
    }
}
        */