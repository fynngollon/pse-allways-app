package com.fynng.allways.statistics


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import java.util.stream.IntStream.range

@Composable
fun StatisticsScreen(navController: NavController, statisticsViewModel: StatisticsViewModel) {

    Column {
        val labels: List<String> = listOf("label1", "label2", "label3")
        val values: List<Int> = listOf(5, 2, 3)
        BarDiagram(labels = labels, values = values)
        LineChart(labels = labels, values = values)
    }
}

@Composable
fun LineChart(labels: List<String>, values: List<Int>) {
    val pointsData: MutableList<Point> = mutableListOf()
    for(i in range(0, labels.size)){
        pointsData.add(Point(i.toFloat(), values[i].toFloat()))
    }
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(pointsData.size -1)
        .labelData{i -> i.toString()}
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(pointsData.size -1)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = 100 / (pointsData.size -1)
            (i * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines()
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}

@Composable
fun BarDiagram(labels: List<String>, values: List<Int>) {
    val stepSize = 5
    val barsData = DataUtils.getBarChartData(
        listSize = 8,
        maxRange = 8,
        barChartType = BarChartType.VERTICAL,
        dataCategoryOptions = DataCategoryOptions()
    )

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .steps(barsData.size - 1)
        .bottomPadding(40.dp)
        .axisLabelAngle(20f)
        .labelData {index -> barsData[index].label}
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(stepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData {index -> (index * (100 / stepSize)).toString()}
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val barchartData = BarChartData(
        chartData = barsData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    BarChart(
        modifier = Modifier
            .height(350.dp),
        barChartData = barchartData
    )

}