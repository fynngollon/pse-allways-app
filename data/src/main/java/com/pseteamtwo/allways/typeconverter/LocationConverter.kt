package com.pseteamtwo.allways.typeconverter

import android.location.Location
import androidx.room.TypeConverter

class LocationConverter {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return "${location.provider},${location.latitude},${location.longitude},${location.time},${location.speed}"
    }

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