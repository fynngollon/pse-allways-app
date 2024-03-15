package com.pseteamtwo.allways.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pseteamtwo.allways.ui.home.HomeScreen
import com.pseteamtwo.allways.ui.login.LoginScreen
import com.pseteamtwo.allways.ui.profile.ProfileScreen
import com.pseteamtwo.allways.ui.settings.SettingsScreen
import com.pseteamtwo.allways.ui.statistics.StatisticsScreen
import com.pseteamtwo.allways.ui.trips.TripsScreen

/**
 * Composable function providing functionality for navigating between the different views of the app
 * @param navController the [NavHostController] for the navigation
 */

@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Trips.route,
        ) {
            TripsScreen(navController = navController)
        }
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.Statistics.route
        ) {
            StatisticsScreen(navController = navController)
        }
        composable(
            route = Screen.Profile.route
        ) {
            ProfileScreen()
        }
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(navController)
        }
        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen()
        }
    }
}





















