package com.pseteamtwo.allways

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        startTracking()

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
}