package com.pseteamtwo.allways.statistics

data class StatisticsUiState(
    val charts: MutableList<ChartUiState> = mutableListOf()
)
