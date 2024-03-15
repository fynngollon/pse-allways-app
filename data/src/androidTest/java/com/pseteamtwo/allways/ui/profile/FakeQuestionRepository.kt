package com.pseteamtwo.allways.ui.profile

import com.pseteamtwo.allways.data.question.Question
import com.pseteamtwo.allways.data.question.QuestionType
import com.pseteamtwo.allways.data.question.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeQuestionRepository : QuestionRepository{

    private val questions: MutableStateFlow<List<Question>> = MutableStateFlow(
        mutableListOf(
            Question("1", "question1", QuestionType.TEXT, listOf("option1", "option2"), "option1"),
            Question("2", "question2", QuestionType.TEXT, listOf("option1", "option2"), "option1"),
            Question("3", "question3", QuestionType.TEXT, listOf("option1", "option2"), "option1")
        )
    )

    override fun observeAll(): Flow<List<Question>> {
        return questions
    }

    override suspend fun loadQuestionnaire() {
    }

    override suspend fun updateAnswer(id: String, answer: String) {
        // Log.d("Tag", questions.value.size.toString())
        for (question in questions.value) {
            if(id == question.id) {
                questions.emit(mutableListOf(
                    Question("1", "question1", QuestionType.TEXT, listOf("option1", "option2"), "updated answer"),
                    Question("2", "question2", QuestionType.TEXT, listOf("option1", "option2"), "option1"),
                    Question("3", "question3", QuestionType.TEXT, listOf("option1", "option2"), "option1")
                ))
            }
        }
    }

    override suspend fun deleteQuestion(id: String) {
    }

    override suspend fun refresh() {
    }

    override suspend fun saveQuestionsToNetwork(idList: List<String>) {
    }
}