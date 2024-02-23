package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

/**TODO("change comment")
 * Representation of a gpsPoint (to be also used outside this data module).
 *
 * A gpsPoint is a wrapper of [Location] which adds an [id] to it.
 * It can be created by recording through [com.pseteamtwo.allways.trip.tracking] tracking
 * or by the user himself. If it is created by the user, [location] should only contain
 * latitude and longitude.
 * A gpsPoint should be part of 2 [Stage]s at most (1 time as beginning and 1 time as ending).
 *
 * @property id The unique identification number of the gpsPoint for saving, editing and retrieving
 * purposes as well as for preventing duplicates.
 * @property location The [Location] which got tracked or described by the user
 * @constructor Creates a gpsPoint with the specified properties.
 */
data class GpsPoint(
    val id: Long,
    val geoPoint: GeoPoint,
    val time: LocalDateTime
)
