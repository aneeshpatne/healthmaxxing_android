package com.aneesh.healthmaxxing.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.ui.graphics.vector.ImageVector


enum class Destination(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    METRICS(
        route = "metrics",
        icon = Icons.Outlined.Analytics,
        label = "Metrics",
        contentDescription = "Metrics Screen"
    ),
    WORKOUTS(
        route = "workouts",
        icon = Icons.Outlined.FitnessCenter,
        label = "Workouts",
        contentDescription = "Workouts Screen"
    ),
    RECORD(
        route = "record",
        icon = Icons.Outlined.History,
        label = "Record",
        contentDescription = "Record Screen"
    ),
    VITALS(
        route = "vitals",
        icon = Icons.Outlined.MonitorHeart,
        label = "Vitals",
        contentDescription = "Vitals Screen"
    )
}
