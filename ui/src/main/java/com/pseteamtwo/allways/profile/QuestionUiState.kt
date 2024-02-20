package com.pseteamtwo.allways.profile

import com.pseteamtwo.allways.question.QuestionType

data class QuestionUiState(
    val id: String,
    val title: String,
    val type: QuestionType,
    val options: List<String>,
    val answer: String
)
