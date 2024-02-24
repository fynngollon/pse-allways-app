package com.pseteamtwo.allways.typeconverter

import androidx.room.TypeConverter
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Deprecated("this converter was used for database reasons" +
        "but is not necessary anymore due to changes made to the database objects")
class ListOfLocalGpsPointConverter {

    private val format = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun from(value: List<LocalGpsPoint>): String {
        // Convert list of LocalGpsPoint to JSON string
        return format.encodeToString(value)
    }

    @TypeConverter
    fun to(value: String): List<LocalGpsPoint> {
        // Convert JSON string back to list of LocalGpsPoint
        return format.decodeFromString<List<LocalGpsPoint>>(value)
    }
}