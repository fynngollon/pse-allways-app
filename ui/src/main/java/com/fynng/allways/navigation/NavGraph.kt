package com.fynng.allways.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fynng.allways.home.HomeScreen
import com.fynng.allways.profile.ProfileScreen
import com.fynng.allways.profile.ProfileViewModel
import com.fynng.allways.statistics.StatisticsScreen
import com.fynng.allways.trips.TripsScreen
import com.pseteamtwo.allways.question.repository.ProfileQuestionRepository


@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {


    var profileQuestionRepository: ProfileQuestionRepository = ProfileQuestionRepository()
    var profileViewModel: ProfileViewModel = ProfileViewModel(profileQuestionRepository)


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
            ProfileScreen(navController = navController, profileViewModel)
        }

    }
}