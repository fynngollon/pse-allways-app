package com.pseteamtwo.allways.ui.profile

import com.pseteamtwo.allways.data.question.QuestionType

/**
 *Representation of the information about one question displayed on the [ProfileScreen]
 *
 */
data class QuestionUiState(
    /**
     *unique identifier of the [QuestionUiState]
     *
     */
    val id: String,

    /**
     *title of the question
     *
     */
    val title: String,

    /**
     *type of the question
     *
     */
    val type: QuestionType,

    /**
     *list of possible answers to the question
     *
     */
    val options: List<String>,

    /**
     *answer to the question
     *
     */
    var answer: String,

    /**
     *Boolean value indication if permission is given to send the answer to the question to the server.
     *
     */
    var sendToServer: Boolean
)
