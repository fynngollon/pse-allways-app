package com.pseteamtwo.allways.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val statisticsRepository: DefaultStatisticsRepository) : ViewModel() {

    private var _statisticsUiState: MutableStateFlow<StatisticsUiState> = MutableStateFlow(StatisticsUiState())
    val statisticsUiState: StateFlow<StatisticsUiState> = _statisticsUiState.asStateFlow()
    var chartUiStates: MutableList<ChartUiState> = mutableListOf()


    init {
        viewModelScope.launch {
            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Distanz aller Wege zusammen",
                listOf("trip distance of all"),
                listOf(statisticsRepository.getTripDistanceOfAll().toLong())
            )
            )
            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Dauer aller Wege zusammen",
                listOf("trip duration of all"),
                listOf(statisticsRepository.getTripDurationOfAll()))
            )


            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Durchschnittliche Distanz",
                listOf("average distance"),
                listOf(statisticsRepository.getAverageTripDistance().toLong()))
            )

            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Durchschnittliche Dauer",
                listOf("average duration"),
                listOf(statisticsRepository.getAverageTripDuration()))
            )

            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Durchschnittliche Geschwindigkeit",
                listOf("average speed"),
                listOf(statisticsRepository.getAverageTripSpeed()))
            )

            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Durchschnittliche Geschwindigkeit",
                listOf("average speed"),
                listOf(statisticsRepository.getAverageTripSpeed()))
            )

            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Durchschnittliche Geschwindigkeit",
                listOf("average speed"),
                listOf(statisticsRepository.getAverageTripSpeed()))
            )

            val completeModalSplit = statisticsRepository.getModalSplitOfAll(true)
            val completeModalSplitLabels: MutableList<String> = mutableListOf()
            val completetModalSplitValues: MutableList<Long> = mutableListOf()

            for(mode in com.pseteamtwo.allways.trip.Mode.values()) {
                completeModalSplitLabels.add(mode.toString())
                completeModalSplit[mode]?.let { completetModalSplitValues.add(it.toLong()) }
            }

            chartUiStates.add(ChartUiState(ChartType.PIE,
                "Anteil Verkehrsmittel",
                completeModalSplitLabels,
                completetModalSplitValues
            )
            )


        }
    }
}






