package com.pseteamtwo.allways.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pseteamtwo.allways.home.HomeScreen
import com.pseteamtwo.allways.profile.ProfileScreen
import com.pseteamtwo.allways.statistics.StatisticsScreen
import com.pseteamtwo.allways.trip.repository.TestTripAndStageRepository
import com.pseteamtwo.allways.trips.TripsScreen
import com.pseteamtwo.allways.trips.TripsViewModel


@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Trips.route
    ) {
        composable(
            route = Screen.Trips.route,
        ) {
            TripsScreen(
                tripsViewModel = TripsViewModel(TestTripAndStageRepository()),
                navController = navController
            )
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
            ProfileScreen(navController = navController)
        }

    }
}
