package com.pseteamtwo.allways.statistics

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
     * The title of the chart displayed above the chart
     */
    var title: String,

    /**
     * The list of labels of this chart indicating what the given information is referring to.
     */
    var labels: List<String>,

    /**
     * The list of values corresponding to the [labels]
     */
    var values: List<Long>,

    /**
     * The unit in which the information is expressed
     */
    var unit: String
)
