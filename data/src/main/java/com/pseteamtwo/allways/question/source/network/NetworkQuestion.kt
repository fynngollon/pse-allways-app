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

fun NetworkQuestion.toLocal() = LocalQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer
)

fun List<NetworkQuestion>.toLocal() : List<LocalQuestion> {
    TODO("Not yet implemented")
}

fun LocalQuestion.toNetwork() = NetworkQuestion(
    id = id,
    title = title,
    type = type,
    options = options,
    answer = answer,
    pseudonym = "" //TODO("need pseudonym as parameter in function call?")
)

fun List<LocalQuestion>.toNetwork() : List<NetworkQuestion> {
    TODO("Not yet implemented")
}