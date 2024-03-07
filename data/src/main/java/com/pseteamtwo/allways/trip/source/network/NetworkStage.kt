package com.pseteamtwo.allways.trip.source.network

import com.pseteamtwo.allways.trip.Mode
import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

/**
 * Representation of a stage for saving to and reading from the network database.
 *
 * @property id The unique identification number of the stage.
 * @property tripId The identification number of the trip this stage belongs to.
 * @property mode The [Mode] this stage got travelled in.
 * @property startDateTime The start time of the stage.
 * @property endDateTime The end time of the stage.
 * @property startLocation The start location of the stage.
 * @property endLocation The end location of the stage.
 * @property duration The duration of the stage.
 * @property distance The distance of the stage.
 * @constructor Creates a [NetworkStage] with the given properties.
 */
data class NetworkStage (
    val id: Long,
    val tripId: Long,
    val mode: Mode,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val startLocation: GeoPoint,
    val endLocation: GeoPoint,
    val duration: Int,
    val distance: Int
)