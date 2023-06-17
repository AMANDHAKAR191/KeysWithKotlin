package com.aman.keyswithkotlin.presentation.navigation

import android.app.Activity
import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aman.keyswithkotlin.presentation.navigation.Screen.AuthScreen
import com.aman.keyswithkotlin.presentation.navigation.Screen.ProfileScreen
import com.aman.keyswithkotlin.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.presentation.auth.AuthScreen

@Composable
@ExperimentalAnimationApi
fun NavGraph(
    navController: NavHostController,
    context: Context, activity: Activity
) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            route = AuthScreen.route
        ) {
            AuthScreen(
                navigateToProfileScreen = {
                    navController.popBackStack()
                    navController.navigate(ProfileScreen.route)
                }
            )
        }
        composable(
            route = ProfileScreen.route
        ) {
            ProfileScreen(
                navigateToAuthScreen = {
                    navController.popBackStack()
                    navController.navigate(AuthScreen.route)
                }
            )
        }
    }
}