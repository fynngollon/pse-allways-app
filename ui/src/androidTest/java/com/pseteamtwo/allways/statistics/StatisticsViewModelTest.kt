package com.pseteamtwo.allways.statistics

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pseteamtwo.allways.trip.Mode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import java.util.stream.IntStream.range

@RunWith(AndroidJUnit4::class)
class StatisticsViewModelTest {



    private lateinit var fakeRepository: FakeStatisticsRepository
    private lateinit var viewModel: StatisticsViewModel

   /* @OptIn(ExperimentalCoroutinesApi::class)
    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    //Class under test
    private lateinit var repository: DefaultStatisticsRepository
*/

    // using an in-memory database because the information stored here disappears when the
    // process is killed
    // Ensure that we use a new database for each test.
    @Before
    fun setUp() {
        fakeRepository = FakeStatisticsRepository()
        viewModel = StatisticsViewModel(fakeRepository)
    }


    @Test
    fun testAddCompleteDistancechart() = runTest {
        viewModel.addCompleteDistanceChart()
        assertNotNull(viewModel.statisticsUiState.value.charts.last())
        assertEquals(viewModel.statisticsUiState.value.charts.last().type, ChartType.SINGLE_VALUE)
        assertEquals(viewModel.statisticsUiState.value.charts.last().values.last(), 50)
    }

    @Test
    fun testAddCompleteDurationChart() = runTest {
        viewModel.addCompleteDurationChart()
        assertNotNull(viewModel.statisticsUiState.value.charts.last())
        assertEquals(viewModel.statisticsUiState.value.charts.last().type, ChartType.SINGLE_VALUE)
        assertEquals(viewModel.statisticsUiState.value.charts.last().values.last(), 50)
    }

    @Test
     fun testAddCompleteModalSplitChart() = runTest{


        var labels: MutableList<String> = mutableListOf<String>(Mode.WALK.modeType, Mode.REGIONAL_BUS.modeType, Mode.OTHER.modeType)

        viewModel.addCompleteModalSplitChart()
        assertEquals(viewModel.statisticsUiState.value.charts.last().labels, labels)
    }


    @Test
    fun testAddDistancesOfLastWeekChart() = runTest{
        var currentDate = LocalDateTime.now().toLocalDate()
        var labels: MutableList<String> = mutableListOf()
        var values: MutableList<Long> = mutableListOf()

        for (i in range(0, 7)) {
            labels.add((currentDate.plusDays((i-6).toLong())).dayOfMonth.toString() + ".")
            values.add(fakeRepository.getTripDistanceOfDate(currentDate.plusDays(
                (i-6).toLong()
            )).toLong())
        }
        viewModel.addTodaysModalSplitChartHome()
        assertEquals(viewModel.statisticsUiState.value.charts.last().labels, labels)
        assertEquals(viewModel.statisticsUiState.value.charts.last().values, values)

    }

    @Test
    fun testAddTodaysDistanceChartHome() = runTest {
        viewModel.addTodaysDistanceChartHome()
        delay(200)
        Log.d("Tag", viewModel.homeStatisticsUiState.value.charts.last().labels.first())
        assertEquals(viewModel.homeStatisticsUiState.value.charts.last().labels, listOf<String>("Distanz"))
        assertEquals(viewModel.homeStatisticsUiState.value.charts.last().values, fakeRepository.getTripDistanceOfDate(LocalDateTime.now().toLocalDate()))
    }

    @Test
    fun testAddTodaysModalSplitChartHome() = runTest {
        var labels: MutableList<String> = mutableListOf<String>(Mode.WALK.modeType, Mode.REGIONAL_BUS.modeType, Mode.OTHER.modeType)

        viewModel.addTodaysModalSplitChartHome()
        assertEquals(viewModel.homeStatisticsUiState.value.charts.first().labels, labels)
    }


    @Test
    fun testAddTodaysDurationChart() = runTest {
        viewModel.addTodaysDurationChart()
        assertNotNull(viewModel.homeStatisticsUiState.value.charts.last())
        assertEquals(viewModel.homeStatisticsUiState.value.charts.last().type, ChartType.COLUMN)
        assertEquals(viewModel.homeStatisticsUiState.value.charts.last().values.last(), 50)
    }
}