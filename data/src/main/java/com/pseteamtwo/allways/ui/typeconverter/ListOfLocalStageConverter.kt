package com.pseteamtwo.allways.ui.typeconverter

import androidx.room.TypeConverter
import com.pseteamtwo.allways.data.trip.source.local.LocalStage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Deprecated("this converter was used for database reasons" +
        "but is not necessary anymore due to changes made to the database objects")
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