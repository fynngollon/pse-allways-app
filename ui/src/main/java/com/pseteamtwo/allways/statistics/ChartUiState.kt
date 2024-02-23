package com.pseteamtwo.allways.statistics

data class ChartUiState(
    var type: ChartType,
    var title: String,
    var labels: List<String>,
    var values: List<Long>
)
