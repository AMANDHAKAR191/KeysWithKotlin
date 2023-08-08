package com.aman.keyswithkotlin.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileViewModel
import com.aman.keyswithkotlin.core.components.BottomBar

fun NavGraphBuilder.settingNavGraph(
    navController: NavController) {
    navigation(
        startDestination = BottomBarScreen.Settings.route,
        route = Graph.SETTING
    ) {
        composable(BottomBarScreen.Settings.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                displayName = viewModel.displayName.invoke(),
                photoUrl = viewModel.photoUrl.invoke(),
                signOutResponse = viewModel.signOutResponse,
                revokeAccessResponse = viewModel.revokeAccessResponse,
                onSignOut = {
                    viewModel.signOut()
                },
                onRevokeAccess = {
                    viewModel.revokeAccess()
                },
                navigateToAuthScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.AUTHENTICATION)
                },
                navigateToPasswordScreen = {
                    navController.navigate(Graph.HOME) {
                        popUpTo(Graph.HOME)
                    }
                },
                bottomBar = {
                    BottomBar(navController, navigateTo = {
                        navController.popBackStack()
                        navController.navigate(it) {
                            launchSingleTop = true
                        }
                    })
                }
            )
        }
    }

}