package com.pseteamtwo.allways.trip.tracking.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionDialog(
    isPermanentlyDeclined: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    val permissionTextProvider = LocationPermissionTextProvider()

    AlertDialog(
        title = {
            Text(text = "Erforderliche Berechtigungen")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isPermanentlyDeclined) {
                        onGoToAppSettingsClick()
                    } else {
                        onConfirmation()
                    }
                }
            ) {
                Text("Berechtigung erteilen")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Abbrechen")
            }
        }
    )
}

class LocationPermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String {
        val description = "Die App benötigt die Standort-Berechtigungen, um Tracking zu nutzen. "
        if(isPermanentlyDeclined) {
            description.plus("Sie können in die App-Einstellungen gehen, um diese zu aktivieren.")
        }
        return description
    }
}