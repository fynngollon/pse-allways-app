package com.pseteamtwo.allways.ui.typeconverter

import androidx.room.TypeConverter


/**
 * This converter converts a List<String> to a String.
 * It is necessary for [androidx.room] so it can insert such elements into the local database.
 *
 * @constructor Creates an instance of this class.
 */
class ListStringConverter {

    /**
     * Converts a String to the according List<String> by splitting it according to [toList].
     *
     * @param value The String to be converted.
     * @return The according List<String>.
     */
    @TypeConverter
    fun fromString(value: String): List<String> {
        if(value == "") {
            return emptyList()
        }
        return value.split(",")
    }

    /**
     * Converts a List<String> to the according String by appending it together.
     *
     * @param list The List<String> to be converted.
     * @return The according String.
     */
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}