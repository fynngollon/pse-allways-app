package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import kotlin.math.roundToInt

data class Stage(
    val id: String,
    val tripId: String,
    val gpsPoints: List<GpsPoint>,
    val mode: Mode,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
) {

    val duration by lazy {
        val zoneId = ZoneId.of("Europe/Paris")
        val zonedStartDateTime = ZonedDateTime.of(startDateTime, zoneId)
        val zonedEndDateTime = ZonedDateTime.of(endDateTime, zoneId)
        Duration.between(zonedStartDateTime, zonedEndDateTime).toMinutes()
    }

    val distance by lazy {
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
