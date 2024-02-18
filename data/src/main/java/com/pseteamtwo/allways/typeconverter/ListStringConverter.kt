package com.pseteamtwo.allways.typeconverter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter


class ListStringConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}