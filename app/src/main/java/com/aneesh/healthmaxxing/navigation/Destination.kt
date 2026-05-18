package com.aneesh.healthmaxxing.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector


enum class Destination(
    val route:String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    HOME(
        route = "home",
        icon = Icons.Default.Home,
        label = "Home",
        contentDescription = "Home Screen"
    ),
    WORKOUTS(
        route = "workouts",
        icon = Icons.Default.FitnessCenter,
        label = "Workouts",
        contentDescription = "Workouts Screen"
    ),
    RECORD(
        route = "record",
        icon = Icons.Default.History,
        label = "Record",
        contentDescription = "Record Screen"
    )
}
