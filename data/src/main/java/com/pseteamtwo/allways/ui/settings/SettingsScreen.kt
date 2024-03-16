package com.pseteamtwo.allways.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pseteamtwo.allways.data.settings.AppPreferences

@Composable
fun SettingsScreen() {

    val settingsViewModel : SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.settingsUiState.collectAsState()
    var isActive: Boolean by remember {
        mutableStateOf(settings.isTrackingEnabled)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Text aligned to the left
        Text(
            text = "Wegeerkennung",
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .weight(1f)
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically)
        )

        val hasPermission = remember { mutableStateOf(true) }

        Switch(
            checked = isActive,
            onCheckedChange = {
                hasPermission.value = settingsViewModel.updateTrackingEnabled(it)
                isActive = it
            },
            modifier = Modifier
                .size(25.dp)
                .padding(end = 16.dp)
        )

        MissingPermissionDialog(hasPermission.value, isActive) {
            hasPermission.value = it
        }

    }
}

@Composable
fun MissingPermissionDialog(
    hasPermission: Boolean,
    isSwitchOn: Boolean,
    onConfirm: (confirm: Boolean) -> Unit
) {
    if (!hasPermission && isSwitchOn) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Berechtigungen fehlen") },
            text = { Text("Sie müssen zur Wegeerkennung Standortberechtigungen in den Einstellungen aktivieren") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(true)
                }) {
                    Text("OK")
                }
            }
        )
    }

}
