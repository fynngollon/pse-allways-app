package com.pseteamtwo.allways.question.repository

import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.QuestionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ProfileQuestionRepository : QuestionRepository {


    override fun observeAll(): Flow<List<Question>> {
        return flowOf(questions())
    }

    override suspend fun loadQuestionnaire() {
        TODO("Not yet implemented")
    }

    suspend fun createQuestion(
        id: String,
        title: String,
        type: QuestionType,
        options: List<String>
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun updateAnswer(id: String, answer: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuestion(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuestionsToNetwork(id: List<String>) {
        TODO("Not yet implemented")
    }
}

fun questions(): List<Question> {
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
}