package com.pseteamtwo.allways.statistics


/**
 *Representation of the information about the statistics displayed on the [StatisticsScreen] or the [HomeScreen]
 *
 */
data class StatisticsUiState(

    /**
     * The list of the [ChartUiState] of each chart being displayed on the [StatisticsScreen]
     */
    val charts: MutableList<ChartUiState> = mutableListOf()
)
