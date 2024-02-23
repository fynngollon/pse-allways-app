package com.pseteamtwo.allways.trip.source.network

import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * From string to local date time converts a string to a localDateTime.
 *
 * @param dateAsString Is the dateTime as a string.
 * @return Is the localDateTime from the string.
 */
internal fun fromStringToLocalDateTime(dateAsString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    return LocalDateTime.parse(dateAsString, formatter)
    }

/**
 * From string to geo point converts a string to a GeoPoint.
 *
 * @param geoPointAsString Is the GeoPoint as a string.
 * @return Is the GeoPoint from the string.
 */
internal fun fromStringToGeoPoint(geoPointAsString: String): GeoPoint {
    val parts = geoPointAsString.split(",")
    if (parts.size != 3) {
        throw IllegalArgumentException("Invalid GeoPoint format: expected latitude,longitude")
    }
    val latitude = parts[0].toDouble()
    val longitude = parts[1].toDouble()
    return GeoPoint(latitude, longitude)
}

/**
 * From string to list of stage ids converts a string to a list of longs.
 *
 * @param listOfStagesAsString Is the lost of longs as a string.
 * @return Is the list of longs from the string.
 */
internal fun fromStringToListOfStageIds(listOfStagesAsString: String): List<Long> {
    return listOfStagesAsString.split(",").map { it.trim().toLong() }
}
