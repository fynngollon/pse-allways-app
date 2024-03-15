package com.pseteamtwo.allways.ui.typeconverter

import android.location.Location
import androidx.room.TypeConverter

/**
 * This converter converts a Location to a String.
 * It is necessary for [androidx.room] so it can insert such elements into the local database.
 *
 * @constructor Creates an instance of this class.
 */
class LocationConverter {

    /**
     * Converts a [Location] to the according String.
     *
     * @param location The [Location] to be converted.
     * @return The according String.
     */
    @TypeConverter
    fun fromLocation(location: Location): String {
        return "${location.provider},${location.latitude},${location.longitude},${location.time},${location.speed}"
    }

    /**
     * Converts a String to the according [Location] by appending it together.
     *
     * @param locationString The String to be converted.
     * @return The according [Location].
     */
    @TypeConverter
    fun toLocation(locationString: String): Location {
        val parts = locationString.split(",")
        return Location(parts[0]).apply {
            latitude = parts[1].toDouble()
            longitude = parts[2].toDouble()
            time = parts[3].toLong()
            speed = parts[4].toFloat()
        }
    }
}