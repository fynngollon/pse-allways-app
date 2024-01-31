package com.pseteamtwo.allways.question.source.network

import com.pseteamtwo.allways.question.QuestionType
import com.pseteamtwo.allways.question.source.local.LocalQuestion

data class NetworkQuestion (
    val id: String,
    val title: String,
    val type: QuestionType,
    val options: List<String>,
    val answer: String, //TODO
    val pseudonym: String
)