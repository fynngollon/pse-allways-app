package com.pseteamtwo.allways.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * composable function for displaying the bottom bar for navigation between the different screens
 * of the app.
 * @param profileQuestions the list of the questions to be send to the server.
 */
@Composable
fun BottomNavigation(navController: NavController, modifier: Modifier = Modifier) {
    val screens = listOf(
        Screen.Home, Screen.Trips, Screen.Statistics, Screen.Profile
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.LightGray
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach {screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                          navController.navigate(screen.route) {
                          }
                },
                label = { Text(text = screen.label)},
                icon = {
                    Icon(imageVector = screen.icon, contentDescription = " NavBar Icon")
                       },
                )
        }

    }
}