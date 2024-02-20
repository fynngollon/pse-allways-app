package com.pseteamtwo.allways.trip

import android.location.Location
import org.osmdroid.util.GeoPoint
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

/**
 * Representation of a trip traveled by the user (to be also used outside this data module).
 *
 * A trip can be created by recording through [com.pseteamtwo.allways.trip.tracking] tracking
 * or by the user himself.
 *
 * @property id The unique identification number of the trip for saving, editing and retrieving
 * purposes as well as for preventing duplicates.
 * @property stages A list of [Stage]s of which the trip consists.
 * If the trip is deleted, every of its [stages] should be deleted as well.
 * @property purpose The [Purpose] the trip got traveled for.
 * @property isConfirmed Indicates, whether the trip is confirmed by the user
 * (trips created through tracking are not confirmed until the user confirms,
 * while trips created by the user are confirmed).
 * @constructor Creates a trip with the specified properties.
 * The properties [startDateTime], [endDateTime], [startLocation], [endLocation], [duration]
 * and [distance] are computed through the provided list of [Stage]s.
 */
data class Trip(
    val id: Long,
    val stages: List<Stage>,
    val purpose: Purpose,
    val isConfirmed: Boolean
) {
    val startDateTime: LocalDateTime
        get() = stages.first().startDateTime

    val endDateTime: LocalDateTime
        get() = stages.last().endDateTime

    val startLocation: Location
        get() = stages.first().startLocation

    val endLocation: Location
        get() = stages.last().endLocation

    val duration: Long
        get() = stages.sumOf { it.duration }

    val distance: Int
        get() = stages.sumOf { it.distance }

}