package com.aneesh.healthmaxxing.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aneesh.healthmaxxing.navigation.OnBoardingScreen

@Composable
fun login() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OnBoardingScreen.Page1.route
    ) {
        composable(OnBoardingScreen.Page1.route) {
            LoginPage1(
                onNext = {
                    navController.navigate(OnBoardingScreen.Page2.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(OnBoardingScreen.Page2.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            )
            LoginPage2()
        }
    }
}
