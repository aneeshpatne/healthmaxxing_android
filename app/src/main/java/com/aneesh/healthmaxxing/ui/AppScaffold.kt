package com.aneesh.healthmaxxing.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aneesh.healthmaxxing.navigation.Destination

@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(Destination.METRICS.ordinal)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = {
            FormaBottomBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { index, destination ->
                    selectedDestination = index
                    navController.navigate(destination.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
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
            composable(Destination.VITALS.route) {
                DestinationText(text = "Vitals")
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

