package com.aneesh.healthmaxxing.navigation

sealed class OnBoardingScreen(val route: String) {
    object Page1 : OnBoardingScreen("onboarding_1")
    object Page2 : OnBoardingScreen("onboarding_2")
    object Page3 : OnBoardingScreen("onboarding_3")
}