package com.pseteamtwo.allways.ui.statistics

/**
 *Representation of the information about one Chart that is needed to represent this chart in the view
 *
 */
data class ChartUiState(
    /**
     * The [ChartType] of the chart
     */
    var type: ChartType,

    /**
     * The content of the chart to determine its title and unit
     */
    var contentType: ChartContent,

    /**
     * The list of labels of this chart indicating what the given information is referring to.
     */
    var labels: List<String>,

    /**
     * The list of values corresponding to the [labels]
     */
    var values: List<Long>
)
