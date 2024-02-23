package com.pseteamtwo.allways.statistics

/**
 * ChartType
 *
 * This enum class describes different types of charts. Every chart has exactly one [ChartType]
 * object indication that the chart is of that [ChartType].
 */
enum class ChartType {
    /**
     * Represents that the type of the chart is [COLUMN]
     */
    COLUMN,

    /**
     * Represents that the type of the chart is [LINE]
     */
    LINE,
    /**
     * Represents that the type of the chart is [PIE]
     */
    PIE,
    /**
     * Represents that the type of the chart is [SINGLE_VALUE]
     */
    SINGLE_VALUE
}