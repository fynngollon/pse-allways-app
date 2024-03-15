package com.pseteamtwo.allways.ui.profile

import com.pseteamtwo.allways.ui.statistics.ChartUiState

/**
 *Representation of the information about the statistics displayed on the [ProfileScreen]
 *
 */
data class ProfileUiState(

    /**
     * The list of the [ChartUiState] for the profile related questions to be displayed
     */
    val profileQuestions: MutableList<QuestionUiState> = mutableListOf(),

    /**
     * The list of the [ChartUiState] for the household related questions to be displayed
     */
    val householdQuestions: MutableList<QuestionUiState> = mutableListOf(),

    /**
     * Boolean value indicating loading
     */
    val loading: Boolean = false,

    /**
     * Boolean value indicating the state of the server connection
     */
    val serverConnectionFailed: Boolean = false

)
