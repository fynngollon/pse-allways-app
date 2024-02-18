package com.fynng.allways.statistics

data class ChartUiState(
    var type: ChartType,
    var labels: List<String>,
    var values: List<Int>
)
