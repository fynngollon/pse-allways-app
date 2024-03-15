package com.pseteamtwo.allways.data.trip.tracking.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel


class PermissionActivity : ComponentActivity() {

    private val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<PermissionViewModel>()
            val dialogQueue = viewModel.visiblePermissionDialogQueue

            val trackingPermissionResultLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    permissionsToRequest.forEach { permission ->
                        viewModel.onPermissionResult(
                            permission = permission,
                            isGranted = permissions[permission] == true
                        )
                    }
                }
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    trackingPermissionResultLauncher.launch(permissionsToRequest)
                }) {
                    Text(text = "Request multiple permission")
                }
                Button(onClick = {
                    finish()
                }) {
                    Text(text = "Cancel")
                }
            }

            dialogQueue
                .reversed()
                .forEach { permission ->
                    PermissionDialog(
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                            permission
                        ),
                        onDismissRequest = viewModel::dismissDialog,
                        onConfirmation = {
                            viewModel.dismissDialog()
                            trackingPermissionResultLauncher.launch(
                                arrayOf(permission)
                            )
                        },
                        onGoToAppSettingsClick = ::openAppSettings
                    )
                }
        }

    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}