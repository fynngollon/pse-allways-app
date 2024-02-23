package com.pseteamtwo.allways.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pseteamtwo.allways.home.HomeScreen
import com.pseteamtwo.allways.profile.ProfileScreen
import com.pseteamtwo.allways.statistics.StatisticsScreen
import com.pseteamtwo.allways.trips.TripsScreen


@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {


    //var statisticsRepository: StatisticsRepository = DefaultStatisticsRepository()
   // var statisticsViewModel: StatisticsViewModel = StatisticsViewModel(statisticsRepository)


    NavHost(
        navController = navController,
        startDestination = Screen.Trips.route
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
            ProfileScreen(navController = navController)
        }

    }
}