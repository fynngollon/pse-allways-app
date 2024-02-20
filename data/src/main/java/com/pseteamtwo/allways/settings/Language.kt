package com.pseteamtwo.allways.settings

import kotlinx.serialization.Serializable

/**
 * Language
 *
 * This enum class describes the languages this application can be viewed in.
 * This language can is set in the settings [AppPreferences] and can be changed by the user.
 *
 * @property languageCode Gives every [Language] object a short string so it can also be accessed
 * through a String.
 */
@Serializable
enum class Language(val languageCode: String) {
    /**
     * The language german.
     */
    GERMAN("de"),

    /**
     * The language english.
     */
    ENGLISH("en");
}