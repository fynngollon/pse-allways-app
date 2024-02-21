package com.pseteamtwo.allways.typeconverter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.pseteamtwo.allways.trip.source.local.LocalGpsPoint
import com.pseteamtwo.allways.trip.source.local.LocalStage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class ListOfLocalStageConverter {

    private val format = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun from(value: List<LocalStage>): String {
        // Convert list of LocalGpsPoint to JSON string
        return format.encodeToString(value)
    }

    @TypeConverter
    fun to(value: String): List<LocalStage> {
        // Convert JSON string back to list of LocalGpsPoint
        return format.decodeFromString<List<LocalStage>>(value)
    }
}