package com.pseteamtwo.allways.trip

import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime

/**
 * Representation of a gpsPoint (to be also used outside this data module).
 *
 * A gpsPoint consists of an unique [id] it is identified by for according databases, a
 * geoPoint (which consists of latitude and longitude) and a time describing when the user was at
 * that geoPoint.
 * It can be created by recording through [com.pseteamtwo.allways.trip.tracking] tracking
 * or by the user himself.
 * A gpsPoint should be part of 2 [Stage]s at most (1 time as beginning and 1 time as ending).
 *
 * @property id The unique identification number of the gpsPoint for saving, editing and retrieving
 * purposes as well as for preventing duplicates.
 * @property geoPoint The [GeoPoint] which got tracked or described by the user
 * @property time The [LocalDateTime] when the user was at the [geoPoint] location.
 * @constructor Creates a gpsPoint with the specified properties.
 */
data class GpsPoint(
    val id: Long,
    val geoPoint: GeoPoint,
    val time: LocalDateTime
)
