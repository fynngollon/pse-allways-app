package com.pseteamtwo.allways.question.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pseteamtwo.allways.question.Question
import com.pseteamtwo.allways.question.QuestionType

@Entity(
    tableName = "question"
)
data class LocalQuestion(
    @PrimaryKey val id: String,
    var title: String,
    var type: QuestionType,
    var options: List<String>,
    var answer: String //TODO
)

fun LocalQuestion.toExternal() = Question(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

fun List<LocalQuestion>.toExternal() : List<Question> {
    TODO("Not yet implemented")
}

fun Question.toLocal() = LocalQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

fun List<Question>.toLocal() : List<LocalQuestion> {
    TODO("Not yet implemented")
}