package com.pseteamtwo.allways.profile

data class ProfileUiState(
    //val questions: MutableList<QuestionUiState> = mutableListOf<QuestionUiState>(),
    val profileQuestions: MutableList<QuestionUiState> = mutableListOf(),
    val householdQuestions: MutableList<QuestionUiState> = mutableListOf(),

    val loading: Boolean = false,
    val serverConnectionFailed: Boolean = false

)
