package com.pseteamtwo.allways.trip

import android.location.Location
import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
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
 * @property mode The [Mode] the stage got traveled in.
 * @property gpsPoints A list of [GpsPoint]s of which the stage consists.
 * If the stage is deleted, every of its [gpsPoints] should be deleted as well.
 * @constructor Creates a stage with the specified properties.
 * The properties [startDateTime], [endDateTime], [startLocation], [endLocation], [duration]
 * and [distance] are computed through the provided list of [GpsPoint]s.
 */
data class Stage(
    val id: Long,
    val mode: Mode,
    val gpsPoints: List<GpsPoint>
) {
    /**
     * Start time of the stage.
     * Calculated as time of the first gpsPoint of this stage.
     */
    val startDateTime: LocalDateTime
       get() = gpsPoints.first().time

    /**
     * End time of the stage.
     * Calculated as time of the last gpsPoint of this stage.
     */
    val endDateTime: LocalDateTime
        get() = gpsPoints.last().time

    /**
     * Start location of the stage.
     * Calculated as location of the first gpsPoint of this stage.
     */
    val startLocation: GeoPoint
        get() = gpsPoints.first().geoPoint

    /**
     * End location of the stage.
     * Calculated as location of the last gpsPoint of this stage.
     */
    val endLocation: GeoPoint
        get() = gpsPoints.last().geoPoint

    /**
     * Duration of the whole stage.
     * Calculated as the difference between [startDateTime] and [endDateTime]
     * while using the current zone defined in [zoneIdOfApp].
     */
    val duration by lazy {
        val zonedStartDateTime = ZonedDateTime.of(startDateTime, zoneIdOfApp)
        val zonedEndDateTime = ZonedDateTime.of(endDateTime, zoneIdOfApp)
        Duration.between(zonedStartDateTime, zonedEndDateTime).toMinutes()
    }

    /**
     * Distance of the whole stage.
     * Calculated as the sum of the distances of the gpsPoints of this stage.
     */
    val distance: Int by lazy {
        (0 until gpsPoints.size - 1).sumOf {
            val result = FloatArray(1)
            Location.distanceBetween(
                gpsPoints[it].geoPoint.latitude,
                gpsPoints[it].geoPoint.longitude,
                gpsPoints[it + 1].geoPoint.latitude,
                gpsPoints[it + 1].geoPoint.longitude,
                result
            )
            result[0].roundToInt()
        }
    }
}
