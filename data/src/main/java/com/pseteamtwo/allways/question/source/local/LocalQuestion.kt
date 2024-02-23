package com.pseteamtwo.allways.question.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pseteamtwo.allways.question.QuestionType
import com.pseteamtwo.allways.typeconverter.ListStringConverter

@Entity(
    tableName = "questions"
)
@TypeConverters(ListStringConverter::class)
internal data class LocalQuestion(
    @PrimaryKey val id: String,
    var title: String,
    var type: QuestionType,
    var options: List<String>,
    var answer: String //TODO
)

