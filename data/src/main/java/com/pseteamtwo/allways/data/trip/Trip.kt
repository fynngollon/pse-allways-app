package com.pseteamtwo.allways.data.trip

import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

/**
 * Representation of a trip traveled by the user (to be also used outside this data module).
 *
 * A trip can be created by recording through [com.pseteamtwo.allways.data.trip.tracking] tracking
 * or by the user himself.
 *
 * @property id The unique identification number of the trip for saving, editing and retrieving
 * purposes as well as for preventing duplicates.
 * @property purpose The [Purpose] the trip got traveled for.
 * @property isConfirmed Indicates, whether the trip is confirmed by the user
 * (trips created through tracking are not confirmed until the user confirms,
 * while trips created by the user are confirmed).
 * @property stages A list of [Stage]s of which the trip consists.
 * If the trip is deleted, every of its [stages] should be deleted as well.
 * @constructor Creates a trip with the specified properties.
 * The properties [startDateTime], [endDateTime], [startLocation], [endLocation], [duration]
 * and [distance] are computed through the provided list of [Stage]s.
 */
data class Trip(
    val id: Long,
    val purpose: Purpose,
    val isConfirmed: Boolean,
    val stages: List<Stage>
) {
    /**
     * Start time of the trip.
     * Calculated as start time of the first stage of this trip.
     */
    val startDateTime: LocalDateTime
        get() = stages.first().startDateTime

    /**
     * End time of the trip.
     * Calculated as end time of the last stage of this trip.
     */
    val endDateTime: LocalDateTime
        get() = stages.last().endDateTime

    /**
     * Start location of the trip.
     * Calculated as start location of the first stage of this trip.
     */
    val startLocation: GeoPoint
        get() = stages.first().startLocation

    /**
     * End location of the trip.
     * Calculated as end location of the last stage of this trip.
     */
    val endLocation: GeoPoint
        get() = stages.last().endLocation

    /**
     * Duration of the whole trip. (Should equal difference [endDateTime] - [startDateTime]
     * if every stage of this trip would start directly after the previous one ended).
     * Calculated as the sum of the durations of the stages of this trip.
     */
    val duration: Long
        get() = stages.sumOf { it.duration }

    /**
     * Distance of the whole trip.
     * Calculated as the sum of the distances of the stages of this trip.
     */
    val distance: Int
        get() = stages.sumOf { it.distance }

}