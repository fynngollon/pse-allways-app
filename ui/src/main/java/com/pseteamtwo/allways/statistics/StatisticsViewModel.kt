package com.pseteamtwo.allways.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.util.stream.IntStream.range
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
                listOf("Distanz"),
                listOf(statisticsRepository.getTripDistanceOfAll().toLong()),
                "Meter"
            )
            )
            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Dauer aller Wege zusammen",
                listOf("Dauer"),
                listOf(statisticsRepository.getTripDurationOfAll().toLong()),
                "Minuten"
            )
            )

            /*chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
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
            )*/

            val completeModalSplit = statisticsRepository.getModalSplitOfAll(true)
            val completeModalSplitLabels: MutableList<String> = mutableListOf()
            val completeModalSplitValues: MutableList<Long> = mutableListOf()

            for(mode in com.pseteamtwo.allways.trip.Mode.values()) {
                if(completeModalSplit.containsKey(mode)) {
                    completeModalSplitLabels.add(mode.toString())
                    completeModalSplit[mode]?.let { completeModalSplitValues.add(it.toLong()) }
                }
            }

            chartUiStates.add(ChartUiState(ChartType.PIE,
                "Anteil Verkehrsmittel insgesamt",
                completeModalSplitLabels,
                completeModalSplitValues,
                ""
            )
            )

            var currentDate = LocalDate.now()
            val distanceLastWeekLabels: MutableList<String> = mutableListOf()
            val distanceLastWeekValues: MutableList<Long> = mutableListOf()
            for(i in range(0, 7)) {
                distanceLastWeekLabels.add((currentDate.plusDays((i-6).toLong())).dayOfMonth.toString() + ".")
                distanceLastWeekValues.add(statisticsRepository.getTripDistanceOfDate(currentDate).toLong())
                //currentDate = currentDate.plusDays(-1)
            }

            chartUiStates.add(ChartUiState(ChartType.COLUMN,
                "Zurückgelegte Distanzen der letzten Woche",
                distanceLastWeekLabels,
                distanceLastWeekValues,
                ""
            )
            )

            _statisticsUiState.update {
                    it ->
                it.copy(
                    charts = chartUiStates,
                )
            }
        }
    }
}






