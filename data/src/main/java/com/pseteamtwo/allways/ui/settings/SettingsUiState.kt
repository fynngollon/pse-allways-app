package com.pseteamtwo.allways.ui.settings

import com.pseteamtwo.allways.data.settings.TrackingRegularity

data class SettingsUiState (
    val isTrackingEnabled: Boolean,
    val trackingRegularity: TrackingRegularity
)