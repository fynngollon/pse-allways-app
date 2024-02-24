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
import java.util.stream.IntStream
import javax.inject.Inject


/**
 * Viewmodel to retrieve and update the statistics related data for the [StatisticsScreen] and
 * [HomeScreen]
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(private val statisticsRepository: DefaultStatisticsRepository) : ViewModel() {

    private var _statisticsUiState: MutableStateFlow<StatisticsUiState> = MutableStateFlow(StatisticsUiState())
    val statisticsUiState: StateFlow<StatisticsUiState> = _statisticsUiState.asStateFlow()
    private var chartUiStates: MutableList<ChartUiState> = mutableListOf()


    private var _homeStatisticsUiState: MutableStateFlow<StatisticsUiState> = MutableStateFlow(StatisticsUiState())
    val homeStatisticsUiState: StateFlow<StatisticsUiState> = _homeStatisticsUiState.asStateFlow()
    private var homeChartUiStates: MutableList<ChartUiState> = mutableListOf()

    init {
        viewModelScope.launch {

            assembleStatisticsScreenUiState()
            _statisticsUiState.update {
                    it ->
                it.copy(
                    charts = chartUiStates,
                )
            }

            assembleHomeScreenUiState()
            _homeStatisticsUiState.update {
                    it ->
                it.copy(
                    charts = homeChartUiStates,
                )
            }
        }
    }

     fun updateStatistics() {
         viewModelScope.launch {
             assembleStatisticsScreenUiState()
             assembleHomeScreenUiState()
         }
    }
    /**
     * calls all the necessary functions to assemble the [statisticsUiState] used for the [StatisticsScreen].
     *
     */

    private suspend fun assembleStatisticsScreenUiState(){
        addCompleteDistanceChart()
        addCompleteDurationChart()
        addCompleteModalSplitChart()
        addDistancesOfLastWeekChart()
    }


    /**
     * calls all the necessary functions to assemble the [homeStatisticsUiState] used for the [HomeScreen]
     */
    private suspend fun assembleHomeScreenUiState(){
        addTodaysDistanceChartHome()
        addTodaysDurationChart()
        addTodaysModalSplitChartHome()
    }

    /**
     * adds a chart showing the accumulated distances of all trips to the list of charts displayed on the [StatisticsScreen].
     * The chart is of type [SingleValue]
     */
    private suspend fun addCompleteDistanceChart() {
            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Distanz aller Wege zusammen",
                listOf("Distanz"),
                listOf(statisticsRepository.getTripDistanceOfAll().toLong()),
                "Meter"
            )
            )

    }

    /**
     * adds a chart showing the accumulated duration of all trips to the list of charts displayed on the [StatisticsScreen].
     * The chart is of type [SingleValue]
     */
    private suspend fun addCompleteDurationChart() {
            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Dauer aller Wege zusammen",
                listOf("Dauer"),
                listOf(statisticsRepository.getTripDurationOfAll().toLong()),
                "Minuten"
            )
            )
    }

    /**
     * adds a chart showing the complete modal split of all trips to the list of charts displayed on the [StatisticsScreen].
     * The chart is of type [Pie]
     */
    private suspend fun addCompleteModalSplitChart() {
            val completeModalSplit = statisticsRepository.getModalSplitOfAll(true)
            val completeModalSplitLabels: MutableList<String> = mutableListOf()
            val completeModalSplitValues: MutableList<Long> = mutableListOf()

            for(mode in com.pseteamtwo.allways.trip.Mode.values()) {
                if(completeModalSplit.containsKey(mode)) {
                    completeModalSplitLabels.add(mode.modeType)
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

    }

    /**
     * adds a chart showing the complete distance of each of the last seven days to the list of
     * charts displayed on the [StatisticsScreen].
     * The chart is of type [BarChart]
     */


    private suspend fun addDistancesOfLastWeekChart() {
            var currentDate = LocalDate.now()
            val distanceLastWeekLabels: MutableList<String> = mutableListOf()
            val distanceLastWeekValues: MutableList<Long> = mutableListOf()
            for(i in IntStream.range(0, 7)) {
                distanceLastWeekLabels.add((currentDate.plusDays((i-6).toLong())).dayOfMonth.toString() + ".")
                distanceLastWeekValues.add(statisticsRepository.getTripDistanceOfDate(currentDate).toLong())
                //currentDate = currentDate.plusDays(-1)
            }

            chartUiStates.add(ChartUiState(ChartType.COLUMN,
                "Zurückgelegte Distanzen der letzten Woche in Metern",
                distanceLastWeekLabels,
                distanceLastWeekValues,
                ""
            )
            )
    }

    /**
     * adds a chart showing the accumulated distance of all trips of the current day to the list of
     * charts displayed on the [StatisticsScreen].
     * The chart is of type [SingleValue]
     */
    private suspend fun addTodaysDistanceChartHome() {
            homeChartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                "Heute zurückgelegte Distanz",
                listOf("Distanz"),
                listOf(statisticsRepository.getTripDistanceOfDate(LocalDate.now()).toLong()),
                "Meter"
            ))

    }

    /**
     * adds a chart showing the modal split of the trips of the current day to the list of
     * charts displayed on the [StatisticsScreen].
     * The chart is of type [BarChart]
     */

    private suspend fun addTodaysModalSplitChartHome() {
            val oneDayModalSplit = statisticsRepository.getModalSplitOfDate(true, LocalDate.now())
            val oneDayModalSplitLabels: MutableList<String> = mutableListOf()
            val oneDayModalSplitValues: MutableList<Long> = mutableListOf()

            for(mode in com.pseteamtwo.allways.trip.Mode.values()) {
                if(oneDayModalSplit.containsKey(mode)) {
                    oneDayModalSplitLabels.add(mode.modeType)
                    oneDayModalSplit[mode]?.let { oneDayModalSplitValues.add(it.toLong()) }
                }
            }

            homeChartUiStates.add(ChartUiState(ChartType.PIE,
                "Anteil Verkehrsmittel heute",
                oneDayModalSplitLabels,
                oneDayModalSplitValues,
                ""
            )
            )
    }

    private suspend fun addTodaysDurationChart() {
        homeChartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
            "Gesamtdauer der heutigen Wege",
            listOf("Dauer"),
            listOf(statisticsRepository.getTripDurationOfDate(LocalDate.now())),
            "Minuten"
            ))
    }


}






