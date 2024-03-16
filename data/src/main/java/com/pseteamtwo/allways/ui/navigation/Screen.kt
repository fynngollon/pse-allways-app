package com.pseteamtwo.allways.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.pseteamtwo.allways.R

/**
 * class containing the routes to the screens for navigation.
 */
sealed class Screen(val route: String, val icon: ImageVector, val label: Int) {
    object Home: Screen(route = "home_screen", icon = Icons.Default.Home, label = R.string.home)
    object Trips: Screen(route = "trips_screen", icon = Icons.Default.Route, label = R.string.trips)
    object Statistics: Screen(route = "statistics_screen", icon = Icons.Default.AutoGraph, label = R.string.statistics)
    object Profile: Screen(route = "profile_screen", icon = Icons.Default.Person, label = R.string.profile)
    object Login: Screen(route = "login_screen", icon = Icons.Default.Settings, label = R.string.login)
    object Settings: Screen(route = "settings_screen", icon = Icons.Default.Settings, label = R.string.settings)

    object BottomBar: Screen(route = "bottomBar_screen", icon = Icons.Default.ScreenRotation, label = R.string.bottom_bar)
}