package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

data class Trip(
    val id: String,
    val stages: List<Stage>,
    val purpose: Purpose,
    val isConfirmed: Boolean,
) {

    val startDateTime
        get() = stages.first().startDateTime

    val endDateTime
        get() = stages.last().endDateTime

    val startLocation
        get() = stages.first().startLocation

    val endLocation
        get() = stages.last().endLocation

    val duration
        get() = stages.sumOf { it.duration }

    val distance
        get() = stages.sumOf { it.distance }

}