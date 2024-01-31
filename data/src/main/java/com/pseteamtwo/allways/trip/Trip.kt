package com.pseteamtwo.allways.trip

import android.location.Location
import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.time.LocalDateTime

data class Trip(
    val id: String,
    val stages: List<Stage>,
    val purpose: Purpose,
    val isConfirmed: Boolean,
) {

    val startDateTime: LocalDateTime
        get() = stages.first().startDateTime

    val endDateTime: LocalDateTime
        get() = stages.last().endDateTime

    val startLocation: Location
        get() = stages.first().startLocation

    val endLocation: Location
        get() = stages.last().endLocation

    val duration: Int
        get() = stages.sumOf { it.duration }

    val distance: Int
        get() = stages.sumOf { it.distance }

}