package com.pseteamtwo.allways.question.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pseteamtwo.allways.question.QuestionType
import com.pseteamtwo.allways.typeconverter.ListStringConverter

/**
 * A question the user can fill in to provide information about himself.
 * This class is a representation of such question to be saved to and retrieved from as an element
 * of the [androidx.room.Database]s [HouseholdQuestionDatabase] and [ProfileQuestionDatabase].
 * (This class cannot be inherited from [com.pseteamtwo.allways.question.Question] or the other
 * way around because of the usage of [androidx.room] in this class).
 *
 * @property id The unique identifier String of the question.
 * @property title The title of the question.
 * @property type The answer type [QuestionType] of the question.
 * @property options The options the user can choose from to answer the question.
 * (Ignored if [type] is [QuestionType.TEXT]).
 * @property answer The answer the user has given for the question.
 * @constructor Creates a question with the given properties.
 */
@Entity(
    tableName = "questions"
)
@TypeConverters(ListStringConverter::class)
data class LocalQuestion(
    @PrimaryKey val id: String,
    var title: String,
    var type: QuestionType,
    var options: List<String>,
    var answer: String
)

