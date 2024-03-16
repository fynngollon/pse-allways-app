package com.pseteamtwo.allways.ui.settings

import com.pseteamtwo.allways.data.settings.Language

data class SettingsUiState (
    val isTrackingEnabled: Boolean,
    val getLanguage: Language
)