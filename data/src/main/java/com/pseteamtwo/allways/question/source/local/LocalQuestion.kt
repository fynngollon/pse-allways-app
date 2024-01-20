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