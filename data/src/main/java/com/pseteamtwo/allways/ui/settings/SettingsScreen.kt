package com.pseteamtwo.allways.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pseteamtwo.allways.R
import com.pseteamtwo.allways.data.settings.TrackingRegularity

@Composable
fun SettingsScreen() {

    val settingsViewModel : SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.settingsUiState.collectAsState()
    var isActive: Boolean by remember {
        mutableStateOf(settings.isTrackingEnabled)
    }

    Column {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_trip_recognition),
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
                    .padding(end = 24.dp)
            )

            MissingPermissionDialog(hasPermission.value, isActive) {
                hasPermission.value = it
                isActive = false
            }

        }

        TrackingRegularitySelection(settings) {
            settingsViewModel.changeTrackingRegularity(it)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingRegularitySelection(
    settings: SettingsUiState,
    onRegularityChange: (TrackingRegularity) -> Unit
) {
    val currentRegularity = settings.trackingRegularity
    var selectedRegularity by remember { mutableStateOf(currentRegularity) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings_tracking_regularity),
            modifier = Modifier
                .weight(4f)
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically)
        )

        var isExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier
                .align(Alignment.CenterVertically)  // Align vertically within Row
                .weight(6f)
                .wrapContentWidth(Alignment.End)
        ) {
            OutlinedTextField(
                value = currentRegularity.getStringForRegularity(),
                onValueChange = {},
                readOnly = true,
                leadingIcon = {

                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                shape = RoundedCornerShape(4.dp),
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                //textStyle = TextStyle.Default.copy(fontSize = 12.sp),
                modifier = Modifier
                    .menuAnchor()
                    .height(45.dp)
                    .width(400.dp)
            )

            ExposedDropdownMenu(expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                for(regularity in TrackingRegularity.entries) {
                    if (regularity != TrackingRegularity.NEVER) {
                        DropdownMenuItem(
                            text = {
                                Text(text = regularity.getStringForRegularity())
                            },
                            onClick = {
                                isExpanded = false
                                selectedRegularity = regularity
                                onRegularityChange(regularity)
                            }
                        )
                    }
                }
            }
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
            text = { Text("Sie müssen zur Wegeerkennung Standortberechtigungen in den Einstellungen aktivieren.") },
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

