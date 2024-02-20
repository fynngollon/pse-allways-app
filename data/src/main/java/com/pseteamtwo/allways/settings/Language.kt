package com.pseteamtwo.allways.settings

import kotlinx.serialization.Serializable

@Serializable
enum class Language(val languageCode: String) {
    GERMAN("de"),
    ENGLISH("en");
}