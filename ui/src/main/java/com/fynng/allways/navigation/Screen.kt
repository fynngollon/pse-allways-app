package com.fynng.allways.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home: Screen(route = "home_screen", icon = Icons.Default.Home, label = "Home")
    object Trips: Screen(route = "trips_screen", icon = Icons.Default.Route, label = "Wege")
    object Statistics: Screen(route = "statistics_screen", icon = Icons.Default.AutoGraph, label = "Statistiken")
    object Profile: Screen(route = "profile_screen", icon = Icons.Default.Person, label = "Profil")
}