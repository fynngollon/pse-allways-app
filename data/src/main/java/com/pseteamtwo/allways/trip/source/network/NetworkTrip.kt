package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.trip.Mode
import com.pseteamtwo.allways.trip.Purpose
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

/**
 * Representation of a trip for saving to and reading from the network database.
 *
 * @property id The unique identification number of the trip.
 * @property stageIds The identification numbers of the stages this trip consists of.
 * @property purpose The [Purpose] this trip got travelled for.
 * @property startDateTime The start time of the trip.
 * @property endDateTime The end time of the trip.
 * @property startLocation The start location of the trip.
 * @property endLocation The end location of the trip.
 * @property duration The duration of the trip.
 * @property distance The distance of the trip.
 * @constructor Creates a [NetworkTrip] with the given properties.
 */
data class NetworkTrip (
    val id: Long,
    val stageIds: List<Long>,
    val purpose: Purpose,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val duration: Int,
    val distance: Int
)