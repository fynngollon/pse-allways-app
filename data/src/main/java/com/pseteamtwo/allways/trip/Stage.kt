package com.pseteamtwo.allways.trip

import android.location.Location
import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import kotlin.math.roundToInt

/**
 * Representation of a stage traveled by the user (to be also used outside this data module).
 *
 * A stage can be created by recording through [com.pseteamtwo.allways.trip.tracking] tracking
 * or by the user himself. A stage should be part of only 1 [Trip].
 *
 * @property id The unique identification number of the stage for saving, editing and retrieving
 * purposes as well as for preventing duplicates.
 * @property gpsPoints A list of [GpsPoint]s of which the stage consists.
 * If the stage is deleted, every of its [gpsPoints] should be deleted as well.
 * @property mode The [Mode] the stage got traveled in.
 * @constructor Creates a stage with the specified properties.
 * The properties [startDateTime], [endDateTime], [startLocation], [endLocation], [duration]
 * and [distance] are computed through the provided list of [GpsPoint]s.
 */
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
