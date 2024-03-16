package com.pseteamtwo.allways.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.pseteamtwo.allways.data.settings.AppPreferences
import com.pseteamtwo.allways.data.trip.tracking.hasLocationPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private var _settingsUiState: MutableStateFlow<SettingsUiState> =
        MutableStateFlow(SettingsUiState(isTrackingEnabled = appPreferences.isTrackingEnabled))
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    fun updateTrackingEnabled(newValue: Boolean): Boolean {
        return if (appPreferences.context.hasLocationPermission()) {
            appPreferences.isTrackingEnabled = newValue
            true
        } else {
            false
        }
    }

}
