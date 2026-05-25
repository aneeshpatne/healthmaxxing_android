package com.aneesh.healthmaxxing.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aneesh.healthmaxxing.navigation.Destination

@Composable
fun AppScaffold() {
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = isSystemInDarkTheme()
    val barColor = if (isDarkTheme) {
        Color(0xFF101114)
    } else {
        colorScheme.surface
    }
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(Destination.METRICS.ordinal)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = {
            Column {
                NavigationBar(
                    containerColor = barColor,
                    contentColor = colorScheme.onSurface,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    Destination.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestination == index,
                            onClick = {
                                selectedDestination = index
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(text = destination.label) },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorScheme.onPrimaryContainer,
                                selectedTextColor = colorScheme.onSurface,
                                indicatorColor = colorScheme.primaryContainer,
                                unselectedIconColor = colorScheme.onSurfaceVariant,
                                unselectedTextColor = colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.METRICS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.METRICS.route) {
                DestinationText(text = "Metrics")
            }
            composable(Destination.WORKOUTS.route) {
                DestinationText(text = "Workouts")
            }
            composable(Destination.RECORD.route) {
                DestinationText(text = "Record")
            }
        }
    }
}

@Composable
private fun DestinationText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}
