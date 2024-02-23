package com.pseteamtwo.allways.statistics


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.util.stream.IntStream.range

@Composable
fun StatisticsScreen(navController: NavController) {

        val labels: List<String> = listOf("mon", "tue", "wnd", "thu", "fri")
        val values: List<Int> = listOf(2, 2, 3, 1, 2)
        LazyColumn() {
            item {
                Row {
                    DetailedStatisticsCard(labels = labels, values = values, title = "Kreis", type = ChartType.PIE)

                }
            }
            item {
                Row(modifier = Modifier.padding(top = 40.dp)) {
                    DetailedStatisticsCard(labels = labels, values = values, title = "Linie", type = ChartType.LINE)
                }
            }
            item {
                Row(modifier = Modifier.padding(top = 40.dp)) {
                    DetailedStatisticsCard(labels = labels, values = values, title = "Balken", type = ChartType.COLUMN)
                }
            }

        }
    }

@Composable
fun DetailedStatisticsCard(labels: List<String>, values: List<Int>, title: String, type: ChartType) {
    /*Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor =  Color.White,
        )
    ) {*/
    Column {
        Row(modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)) {
            Text(text = title, fontSize = 25.sp)
        }
        Row {
            when (type) {
                ChartType.COLUMN ->
                    BarChart(labels = labels, values = values, title = title)

                ChartType.LINE ->
                    LineChart(labels = labels, values = values, title = title)

                ChartType.PIE ->
                    Pie(labels = labels, values = values, title = title)

                else -> {}
            }
        }
    }


}



@Composable
fun LineChart(
    labels: List<String>, values: List<Int>, title: String
) {

    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> if(x <= labels.size) {labels[x.toInt() - 1]} else {""}}

    var data = mutableListOf<FloatEntry>()
    for(i in range(0, labels.size)) {
        data.add(FloatEntry((i+1).toFloat(), values[i].toFloat()))
    }

    val chartEntryModel = entryModelOf(data)
    
    Column {


        Row(modifier = Modifier.padding(start = 20.dp)) {
            Chart(
                chart = lineChart(
                    spacing = 5.dp,
                    lines = listOf(
                        com.patrykandpatrick.vico.core.chart.line.LineChart.LineSpec(
                            lineThicknessDp = 0.5f,
                            lineColor = MaterialTheme.colorScheme.tertiary.toArgb(),
                            lineBackgroundShader = DynamicShaders.fromBrush(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceTint.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                        MaterialTheme.colorScheme.surfaceTint.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                                    )
                                )
                            )
                        )
                    ),
                ),
                model = chartEntryModel,
                startAxis = rememberStartAxis(
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = (values.maxOrNull()?: 0) + 1),
                    guideline = null
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = bottomAxisValueFormatter,
                    itemPlacer = AxisItemPlacer.Horizontal.default(
                        spacing = 1,
                        offset = 10,
                        shiftExtremeTicks = false
                    ),
                    guideline = null,
                    tickLength = 0.dp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
        
    }


}


@Composable
fun BarChart(
    labels: List<String>, values: List<Int>, title: String
) {

    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> if(x <= labels.size) {labels[x.toInt() - 1]} else {""}}

    var data = mutableListOf<FloatEntry>()
    for(i in range(0, labels.size)) {
        data.add(FloatEntry((i+1).toFloat(), values[i].toFloat()))
    }

    val chartEntryModel = entryModelOf(data)
    
    Column {

        Row(modifier = Modifier.padding(start = 20.dp)) {
            Chart(
                chart = columnChart(
                ),
                model = chartEntryModel,
                startAxis = rememberStartAxis(
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = (values.maxOrNull()?: 0) + 1),
                    guideline = null
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = bottomAxisValueFormatter,
                    itemPlacer = AxisItemPlacer.Horizontal.default(
                        spacing = 1,
                        offset = 10,
                        shiftExtremeTicks = false
                    ),
                    guideline = null,
                    tickLength = 0.dp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

@Composable
fun Pie(labels: List<String>, values: List<Int>, title: String) {
    var slices: MutableList<PieChartData.Slice> = mutableListOf()
    val colors: List<Color> = mutableListOf(
        Color(0xFF0A3E11),
        Color(0xFF03DAC6),
        Color(0xFF95B8D1),
        Color(0xFFF53844),
        Color(0xffBD5FB5),
        Color(0xff000000)

    )
    for (i in range(0, labels.size)) {
        slices.add(PieChartData.Slice(values[i].toString() + " %", values[i].toFloat(), colors[i % colors.size]))
    }

    val pieChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Pie
    )

    val pieChartConfig = PieChartConfig(
        showSliceLabels = true,
        isAnimationEnable = false,
        activeSliceAlpha = 0.5f,
        animationDuration = 600,
    )
    Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
            for(i in range(0, labels.size)) {

                Column() {
                    Box(modifier = Modifier
                        .padding(start = 20.dp)
                        .size(10.dp, 10.dp)
                        .background(colors[i % colors.size])
                    )
                }
                Column {
                    Text(text = labels[i])
                }
                
            }
        }
        Row(modifier = Modifier.padding(start = 40.dp, top = 30.dp)) {
            PieChart(modifier = Modifier
                .width(200.dp)
                .height(200.dp),
                pieChartData = pieChartData,
                pieChartConfig = pieChartConfig)
        }
    }

}


