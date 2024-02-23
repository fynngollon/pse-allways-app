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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    //val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    val statistics by statisticsViewModel.statisticsUiState.collectAsState()
    val chartUiStates = statistics.charts

    var state: String by remember { mutableStateOf("test") }

        val labels: List<String> = listOf("mon", "tue", "wnd", "thu", "fri")
        val values: List<Int> = listOf(2, 2, 3, 1, 2)
        LazyColumn() {
            /*item {
                Row {
                    DetailedStatisticsCard(labels = labels, values = values, title = state, type = ChartType.PIE)

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
            item {
                Row(modifier = Modifier.padding(top = 40.dp)) {
                    DetailedStatisticsCard(labels = labels, values = values, title = "Single value", type = ChartType.SINGLE_VALUE)
                }
            }*/
            for (chartUiState in chartUiStates) {
                item {
                    Row(modifier = Modifier.padding(top = 40.dp)) {
                        DetailedStatisticsCard(
                            labels = chartUiState.labels,
                            values = chartUiState.values,
                            title = chartUiState.title,
                            type = chartUiState.type
                        )
                    }
                }
            }

        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedStatisticsCard(labels: List<String>, values: List<Long>, title: String, type: ChartType) {
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

            Column {
                Text(text = title, fontSize = 25.sp)
            }
            ////////////////////////////////////////

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 50.dp),
                horizontalAlignment = Alignment.End) {

                var isExpanded: Boolean by remember {
                    mutableStateOf(false)
                }

                var textFieldValue: String by remember {
                    mutableStateOf("7 Tage")
                }


               /* ExposedDropdownMenuBox(
                    modifier = Modifier
                        .padding()
                        .width(150.dp),
                    expanded = isExpanded,
                    onExpandedChange = {
                        isExpanded = it
                    }) {

                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        textStyle = TextStyle.Default.copy(fontSize = 13.sp),
                        modifier = Modifier
                            .menuAnchor()
                            .height(50.dp)
                    )

                    ExposedDropdownMenu(expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {

                        DropdownMenuItem(
                            text = {
                                Text("1 Tag")
                            },
                            onClick = {
                                isExpanded = false
                                textFieldValue = "1 Tag"
                            })

                        DropdownMenuItem(
                            text = {
                                Text("7 Tage")
                            },
                            onClick = {
                                isExpanded = false
                                textFieldValue = "7 Tage"
                            })

                        DropdownMenuItem(
                            text = {
                                Text("ganze Zeit")
                            },
                            onClick = {
                                isExpanded = false
                                textFieldValue = "ganze Zeit"
                            })
                    }*/
                }
            }


            ///////////////////////////////////////
        }
        Row {
            when (type) {
                ChartType.COLUMN ->
                    BarChart(labels = labels, values = values, title = title)

                ChartType.LINE ->
                    LineChart(labels = labels, values = values, title = title)

                ChartType.PIE ->
                    Pie(labels = labels, values = values, title = title)
                ChartType.SINGLE_VALUE ->
                    SingleValue(title = title, label = labels[0], value = values[0].toLong())

                else -> {}
            }
        }
    }


@Composable
fun SingleValue(title: String, label: String, value: Long){
    Row(modifier = Modifier.padding(15.dp)) {
        Column {
            Text(text = label)
        }
        Column {
            Text(text = ": $value")
        }
    }
}


@Composable
fun LineChart(
    labels: List<String>, values: List<Long>, title: String
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
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = ((values.maxOrNull()?: 0) + 1).toInt()),
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
    labels: List<String>, values: List<Long>, title: String
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
                    itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = ((values.maxOrNull()?: 0) + 1).toInt()),
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
fun Pie(labels: List<String>, values: List<Long>, title: String) {
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


