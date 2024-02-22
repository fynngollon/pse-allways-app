package com.pseteamtwo.allways.profile

data class ProfileUiState(
    //val questions: MutableList<QuestionUiState> = mutableListOf<QuestionUiState>(),
    val questions: MutableList<QuestionUiState> = mutableListOf(),

    val loading: Boolean = false,
    val serverConnectionFailed: Boolean = false

)
