package com.pseteamtwo.allways.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * class containing the routes to the screens for navigation.
 */
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home: Screen(route = "home_screen", icon = Icons.Default.Home, label = "Home")
    object Trips: Screen(route = "trips_screen", icon = Icons.Default.Route, label = "Wege")
    object Statistics: Screen(route = "statistics_screen", icon = Icons.Default.AutoGraph, label = "Statistiken")
    object Profile: Screen(route = "profile_screen", icon = Icons.Default.Person, label = "Profil")
    object Login: Screen(route = "login_screen", icon = Icons.Default.Settings, label = "Login")
    object Settings: Screen(route = "settings_screen", icon = Icons.Default.Settings, label = "Einstellungen")

    object BottomBar: Screen(route = "bottomBar_screen", icon = Icons.Default.ScreenRotation, label = "bottom bar")
}