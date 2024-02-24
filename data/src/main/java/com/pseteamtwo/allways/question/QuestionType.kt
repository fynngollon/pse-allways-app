package com.pseteamtwo.allways.question

import kotlinx.serialization.Serializable

/**
 * Defines the answer type of a [Question].
 * A [Question] has exactly one [QuestionType].
 */
@Serializable
enum class QuestionType {
    /**
     * The [Question] can be answered through a text insertion.
     */
    TEXT,

    /**
     * The [Question] can be answered by choosing the option through a radio button.
     */
    RADIO_BUTTON,

    /**
     * The [Question] can be answered by choosing the option through a checked checkbox.
     */
    CHECKBOX,

    /**
     * The [Question] can be answered by choosing the option through a spinner.
     */
    SPINNER
}