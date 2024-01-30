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
    val distance: Int
) {

    val startDateTime
        get() = stages.first().startDateTime

    val endDateTime
        get() = stages.last().endDateTime

    val startLocation
        get() = stages.first().startLocation

    val endLocation
        get() = stages.last().endLocation

    val duration: Long by lazy {
        val zoneId = ZoneId.of("Europe/Paris")
        val zonedStartDateTime = ZonedDateTime.of(startDateTime, zoneId)
        val zonedEndDateTime = ZonedDateTime.of(endDateTime, zoneId)
        Duration.between(zonedStartDateTime, zonedEndDateTime).toMinutes()
    }




}