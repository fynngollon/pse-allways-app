package com.pseteamtwo.allways.data.settings

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
enum class Language(private val languageCode: String) {
    /**
     * The language german.
     */
    GERMAN("de"),

    /**
     * The language english.
     */
    ENGLISH("en");

    companion object {
        /**
         * Returns the language specified by its [languageCode].
         * Returns [GERMAN] as a default value.
         *
         * @param languageString The string by which the according language should be determined
         * through its [languageCode].
         * @return The specified language, if present (else [GERMAN]).
         */
        fun getLanguageFromString(languageString: String): Language {
            Language.entries.forEach { language ->
                if(language.languageCode == languageString)
                    return language
            }
            return GERMAN
        }
    }

    /**
     * Returns the string representation of a language object in form of its [languageCode].
     *
     * @return The [languageCode] of the language.
     */
    override fun toString(): String {
        return this.languageCode
    }
}