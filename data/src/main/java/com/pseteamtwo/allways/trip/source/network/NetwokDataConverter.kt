package com.pseteamtwo.allways.trip.source.network

import org.osmdroid.util.GeoPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

internal fun fromStringToLocalDateTime(dateAsString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    return LocalDateTime.parse(dateAsString, formatter)
    }

internal fun fromStringToGeoPoint(geoPointAsString: String): GeoPoint {
    val parts = geoPointAsString.split(",")
    if (parts.size != 3) {
        throw IllegalArgumentException("Invalid GeoPoint format: expected latitude,longitude")
    }
    val latitude = parts[0].toDouble()
    val longitude = parts[1].toDouble()
    return GeoPoint(latitude, longitude)
}

internal fun fromStringToListOfStageIds(listOfStagesAsString: String): List<Long> {
    return listOfStagesAsString.split(",").map { it.trim().toLong() }
}
