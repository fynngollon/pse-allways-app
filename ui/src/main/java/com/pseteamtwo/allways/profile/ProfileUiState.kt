package com.pseteamtwo.allways.profile

data class ProfileUiState(
    //val questions: MutableList<QuestionUiState> = mutableListOf<QuestionUiState>(),
    val questions: List<QuestionUiState> = emptyList(),

    val loading: Boolean = false,
    val serverConnectionFailed: Boolean = false

)
