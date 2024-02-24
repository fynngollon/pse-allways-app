package com.pseteamtwo.allways

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pseteamtwo.allways.navigation.BottomNavigation

/**
 * composable function for displaying the bottom bar used for navigating between the screens.
 */
@Composable
fun BottomBarScreen(navController: NavHostController) {

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                BottomNavigation(navController = navController)
            }
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
        }
    }
}