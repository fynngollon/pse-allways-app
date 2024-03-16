package com.pseteamtwo.allways

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import com.pseteamtwo.allways.ui.navigation.BottomNavigation
import com.pseteamtwo.allways.ui.navigation.SetUpNavGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * The activity that is launched when the application is started.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        requestLocationPermissions()

        setContent {
            navController = rememberNavController()
            var showBottomBar by rememberSaveable { mutableStateOf(true) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            showBottomBar = when (navBackStackEntry?.destination?.route) {
                "login_screen" -> false // on this screen bottom bar should be hidden
                else -> true
            }

            Scaffold(
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        if(showBottomBar)
                        {
                            BottomNavigation(navController = navController)
                        }
                    }
                },

                ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                ) {
                    SetUpNavGraph(navController = navController)
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private var hasShownPermissionDialog = false

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.all { it.value }
        if (granted) {
            startTracking()
        } else {
            if (!hasShownPermissionDialog) {
                showPermissionExplanationDialog()
                hasShownPermissionDialog = true
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        val builder = AlertDialog.Builder(this)
        val message = if (!hasShownPermissionDialog) {
            getString(R.string.msg_permission_explanation)
        } else {
            getString(R.string.msg_permission_denied)
        }
        builder.setTitle(getString(R.string.trip_recognition_needs_permission))
            .setMessage(message)
            .setCancelable(false) // Optional: prevent dismissal by tapping outside
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                requestLocationPermissions() // Request permission again after explanation
            }
        builder.create().show()
    }
}