package com.pseteamtwo.allways.ui.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pseteamtwo.allways.data.statistics.StatisticsRepository
import com.pseteamtwo.allways.data.trip.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.stream.IntStream
import javax.inject.Inject


/**
 * Viewmodel to retrieve and update the statistics related data for the [StatisticsScreen] and
 * [HomeScreen]
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(private val statisticsRepository: StatisticsRepository) : ViewModel() {

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
             /*homeChartUiStates = mutableListOf()
             chartUiStates = mutableListOf()
             assembleStatisticsScreenUiState()
             assembleHomeScreenUiState()*/
         }
    }
    /**
     * calls all the necessary functions to assemble the [statisticsUiState] used for the [StatisticsScreen].
     *
     */

     suspend fun assembleStatisticsScreenUiState(){
        addCompleteDistanceChart()
        addCompleteDurationChart()
        addCompleteModalSplitChart()
        addDistancesOfLastWeekChart()
    }


    /**
     * calls all the necessary functions to assemble the [homeStatisticsUiState] used for the [HomeScreen]
     */
     suspend fun assembleHomeScreenUiState(){
        addTodaysDistanceChartHome()
        addTodaysDurationChart()
        addTodaysModalSplitChartHome()
    }

    /**
     * adds a chart showing the accumulated distances of all trips to the list of charts displayed on the [StatisticsScreen].
     * The chart is of type [SingleValue]
     */
     suspend fun addCompleteDistanceChart() {
            chartUiStates.add(ChartUiState(ChartType.SINGLE_VALUE,
                ChartContent.DISTANCE_ALL,
                listOf("Distanz"),
                listOf(statisticsRepository.getTripDistanceOfAll().toLong())
            )
            )
    }

    /**
     * adds a chart showing the accumulated duration of all trips to the list of charts displayed on the [StatisticsScreen].
     * The chart is of type [SingleValue]
     */
     suspend fun addCompleteDurationChart() {
            chartUiStates.add(ChartUiState(
                ChartType.SINGLE_VALUE,
                ChartContent.DURATION_ALL,
                listOf("Dauer"),
                listOf(statisticsRepository.getTripDurationOfAll().toLong())
            )
            )
    }

    /**
     * adds a chart showing the complete modal split of all trips to the list of charts displayed on the [StatisticsScreen].
     * The chart is of type [Pie]
     */
     suspend fun addCompleteModalSplitChart() {
            val completeModalSplit = statisticsRepository.getModalSplitOfAll(true)
            val completeModalSplitLabels: MutableList<String> = mutableListOf()
            val completeModalSplitValues: MutableList<Long> = mutableListOf()

            for(mode in Mode.entries) {
                if(completeModalSplit.containsKey(mode)) {
                    completeModalSplitLabels.add(mode.modeType)
                    completeModalSplit[mode]?.let { completeModalSplitValues.add(it.toLong()/10) }
                }
            }


            chartUiStates.add(ChartUiState(
                ChartType.PIE,
                ChartContent.MODAL_SPLIT_ALL,
                completeModalSplitLabels,
                completeModalSplitValues
            )
            )

    }

    /**
     * adds a chart showing the complete distance of each of the last seven days to the list of
     * charts displayed on the [StatisticsScreen].
     * The chart is of type [BarChart]
     */


     suspend fun addDistancesOfLastWeekChart() {
            var currentDate = LocalDateTime.now().toLocalDate()
            val distanceLastWeekLabels: MutableList<String> = mutableListOf()
            val distanceLastWeekValues: MutableList<Long> = mutableListOf()
            for(i in IntStream.range(0, 7)) {
                distanceLastWeekLabels.add((currentDate.plusDays((i-6).toLong())).dayOfMonth.toString() + ".")
                distanceLastWeekValues.add(statisticsRepository.getTripDistanceOfDate(currentDate.plusDays(
                    (i-6).toLong()
                ))
                    .toLong())
                //currentDate = currentDate.plusDays(-1)
            }

            chartUiStates.add(ChartUiState(
                ChartType.COLUMN,
                ChartContent.DISTANCE_LAST_WEEK,
                distanceLastWeekLabels,
                distanceLastWeekValues
            )
            )
    }

    /**
     * adds a chart showing the accumulated distance of all trips of the current day to the list of
     * charts displayed on the [StatisticsScreen].
     * The chart is of type [SingleValue]
     */
     suspend fun addTodaysDistanceChartHome() {
            homeChartUiStates.add(ChartUiState(
                ChartType.SINGLE_VALUE,
                ChartContent.DISTANCE_TODAY,
                listOf("Distanz"),
                listOf(statisticsRepository.getTripDistanceOfDate(LocalDate.now()).toLong())
            ))

    }

    /**
     * adds a chart showing the modal split of the trips of the current day to the list of
     * charts displayed on the [StatisticsScreen].
     * The chart is of type [BarChart]
     */

     suspend fun addTodaysModalSplitChartHome() {
            val oneDayModalSplit = statisticsRepository.getModalSplitOfDate(true, LocalDate.now())
            val oneDayModalSplitLabels: MutableList<String> = mutableListOf()
            val oneDayModalSplitValues: MutableList<Long> = mutableListOf()

            for(mode in Mode.entries) {
                if(oneDayModalSplit.containsKey(mode)) {
                    oneDayModalSplitLabels.add(mode.modeType)
                    oneDayModalSplit[mode]?.let { oneDayModalSplitValues.add(it.toLong()/10) }
                }
            }

            homeChartUiStates.add(ChartUiState(
                ChartType.PIE,
                ChartContent.MODAL_SPLIT_TODAY,
                oneDayModalSplitLabels,
                oneDayModalSplitValues
            )
            )
    }

    suspend fun addTodaysDurationChart() {
        homeChartUiStates.add(ChartUiState(
            ChartType.SINGLE_VALUE,
            ChartContent.DURATION_TODAY,
            listOf("Dauer"),
            listOf(statisticsRepository.getTripDurationOfDate(LocalDate.now()))
            ))
    }

}






