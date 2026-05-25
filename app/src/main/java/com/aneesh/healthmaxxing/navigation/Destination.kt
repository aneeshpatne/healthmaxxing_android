package com.aneesh.healthmaxxing.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.ui.graphics.vector.ImageVector


enum class Destination(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    METRICS(
        route = "metrics",
        icon = Icons.Default.Analytics,
        label = "Metrics",
        contentDescription = "Metrics Screen"
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
    ),
    VITALS(
        route = "vitals",
        icon = Icons.Default.MonitorHeart,
        label = "Vitals",
        contentDescription = "Vitals Screen"
    )
}

