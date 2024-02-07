package com.pseteamtwo.allways.trip

import android.location.Location
import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import kotlin.math.roundToInt

data class Stage(
    val id: Long,
    val gpsPoints: List<GpsPoint>,
    val mode: Mode,
) {

    val startDateTime: LocalDateTime by lazy {
        val timeInMillis: Long = gpsPoints.first().location.time
        val instant: Instant = Instant.ofEpochMilli(timeInMillis)
        val zoneId = ZoneId.of("Europe/Berlin")
        LocalDateTime.ofInstant(instant, zoneId)
    }

    val endDateTime: LocalDateTime by lazy {
        val timeInMillis: Long = gpsPoints.last().location.time
        val instant: Instant = Instant.ofEpochMilli(timeInMillis)
        val zoneId = ZoneId.of("Europe/Berlin")
        LocalDateTime.ofInstant(instant, zoneId)
    }

    val startLocation: Location
        get() = gpsPoints.first().location

    val endLocation: Location
        get() = gpsPoints.last().location

    val duration by lazy {
        val zoneId = ZoneId.of("Europe/Berlin")
        val zonedStartDateTime = ZonedDateTime.of(startDateTime, zoneId)
        val zonedEndDateTime = ZonedDateTime.of(endDateTime, zoneId)
        Duration.between(zonedStartDateTime, zonedEndDateTime).toMinutes()
    }

    val distance: Int by lazy {
        (0 until gpsPoints.size - 1).sumOf {
            val result = FloatArray(1)
            Location.distanceBetween(
                gpsPoints[it].location.latitude,
                gpsPoints[it].location.longitude,
                gpsPoints[it + 1].location.latitude,
                gpsPoints[it + 1].location.longitude,
                result
            )
            result[0].roundToInt()
        }
    }
}
