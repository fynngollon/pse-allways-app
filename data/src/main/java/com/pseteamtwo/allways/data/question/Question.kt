package com.pseteamtwo.allways.data.question

/**
 * A question the user can fill in to provide information about himself.
 * This class is a representation of such question to also be exposed to other layers
 * of the architecture.
 *
 * @property id The unique identifier String of the question.
 * @property title The title of the question.
 * @property type The answer type [QuestionType] of the question.
 * @property options The options the user can choose from to answer the question.
 * (Ignored if [type] is [QuestionType.TEXT]).
 * @property answer The answer the user has given for the question.
 * @constructor Creates a question with the given properties.
 */
data class Question(
    var id: String,
    var title: String,
    var type: QuestionType,
    var options: List<String>,
    var answer: String
)
