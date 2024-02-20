package com.pseteamtwo.allways.statistics

import androidx.lifecycle.ViewModel
import com.pseteamtwo.allways.statistics.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StatisticsViewModel(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private var _statisticsUiState: MutableStateFlow<StatisticsUiState> = MutableStateFlow(StatisticsUiState())
    val statisticsUiState: StateFlow<StatisticsUiState> = _statisticsUiState.asStateFlow()

    init {
        /*var chartUiStates: MutableList<ChartUiState> = mutableListOf()
        chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
            listOf("trip distance of all"),
            listOf(statisticsRepository.getTripDistanceOfAll()))
        )
        chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
            listOf("trip duration of all"),
            listOf(statisticsRepository.getTripDurationOfAll()))
        )
*/
    }
}