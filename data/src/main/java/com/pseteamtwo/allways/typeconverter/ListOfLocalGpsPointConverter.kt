package com.pseteamtwo.allways.typeconverter

import androidx.room.TypeConverter
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import kotlinx.serialization.json.Json

@TypeConverter
class ListOfLocalGpsPointConverter {

    @TypeConverter
    fun fromJson(json: String): List<LocalGpsPoint> {
        // Use Kotlinx Serialization for JSON parsing
        val format = Json.decodeFromString<List<LocalGpsPoint>>(json)
        return format
    }

    @TypeConverter
    fun toJson(points: List<LocalGpsPoint>): String {
        // Use Kotlinx Serialization for JSON serialization
        val json = Json.encodeToString(points)
        return json
    }
}