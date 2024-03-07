package com.pseteamtwo.allways.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pseteamtwo.allways.home.HomeScreen
import com.pseteamtwo.allways.login.LoginScreen
import com.pseteamtwo.allways.profile.ProfileScreen
import com.pseteamtwo.allways.settings.SettingsScreen
import com.pseteamtwo.allways.statistics.StatisticsScreen
import com.pseteamtwo.allways.trips.TripsScreen

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





















