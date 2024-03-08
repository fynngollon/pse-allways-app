package com.pseteamtwo.allways.permission

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
            Text(text = "Erforderliche Berechtigungen zum Tracking")
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
        val description = "Die App benoetigt die Standort-Berechtigungen, um Tracking zu nutzen. "
        if(isPermanentlyDeclined) {
            description.plus("Sie koennen in die App-Einstellungen gehen, um diese zu aktivieren.")
        }
        return description
    }
}