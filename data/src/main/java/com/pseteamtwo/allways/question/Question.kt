package com.pseteamtwo.allways.question

data class Question(
    var id: String,
    var title: String,
    var type: QuestionType,
    var options: List<String>,
    var answer: String //TODO
)
