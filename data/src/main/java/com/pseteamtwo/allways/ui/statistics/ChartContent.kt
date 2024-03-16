package com.pseteamtwo.allways.ui.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pseteamtwo.allways.R

/**
 * ChartContent
 *
 * This enum class describes what the content of a chart is to determine the right output for the
 * ui.
 */
enum class ChartContent(private val contentTitleId: Int, private val contentUnitId: Int) {
    /**
     * Distance of all trips.
     */
    DISTANCE_ALL(R.string.distance_all, R.string.unit_meters),

    /**
     * Duration of all trips.
     */
    DURATION_ALL(R.string.duration_all, R.string.unit_minutes),

    /**
     * Modal split of all trips.
     */
    MODAL_SPLIT_ALL(R.string.modal_split_all, R.string.unit_nothing),

    /**
     * Distance of all trips of the last week.
     */
    DISTANCE_LAST_WEEK(R.string.distance_last_week, R.string.unit_nothing),

    /**
     * Distance of all trips of today.
     */
    DISTANCE_TODAY(R.string.distance_today, R.string.unit_meters),

    /**
     * Duration of all trips of today.
     */
    DURATION_TODAY(R.string.duration_today, R.string.unit_minutes),

    /**
     * Modal split of all trips of today.
     */
    MODAL_SPLIT_TODAY(R.string.modal_split_today, R.string.unit_nothing);


    @Composable
    fun getTitleForChartContent(): String {
        return stringResource(id = this.contentTitleId)
    }

    @Composable
    fun getUnitForChartContent(): String {
        return stringResource(id = this.contentUnitId)
    }
}