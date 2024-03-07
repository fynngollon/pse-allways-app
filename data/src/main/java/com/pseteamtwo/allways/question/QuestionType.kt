package com.pseteamtwo.allways.question

import kotlinx.serialization.Serializable

@Serializable
enum class QuestionType {
    TEXT,
    RADIO_BUTTON,
    CHECKBOX,
    SPINNER
}