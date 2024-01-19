package com.aman.keys.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keys.on_boarding_screens.OnBoardingScreen

fun NavGraphBuilder.onBoardingGraph(
    navController: NavController
) {
    navigation(
        route = Graph.ON_BOARDING,
        startDestination = Screen.OnBoardingScreen.route
    ) {
        composable(
            route = Screen.OnBoardingScreen.route
        ) {
//            val viewmodel = viewmodels()
            OnBoardingScreen(
                navigateToAuthScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.AUTHENTICATION)
                }
            )
        }
    }
}