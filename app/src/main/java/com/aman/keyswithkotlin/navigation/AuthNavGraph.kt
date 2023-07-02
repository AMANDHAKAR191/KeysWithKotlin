package com.aman.keyswithkotlin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = Screen.AuthScreen.route
    ) {

    }

}