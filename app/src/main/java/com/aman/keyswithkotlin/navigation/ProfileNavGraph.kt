package com.aman.keyswithkotlin.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.auth.presentation.auth.AuthViewModel
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileScreen
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileViewModel
import com.aman.keyswithkotlin.core.components.BottomBar
import com.google.firebase.auth.GoogleAuthProvider

fun NavGraphBuilder.profileNavGraph(
    navController: NavController
) {
    navigation(
        route = Graph.PROFILE,
        startDestination = Screen.ProfileScreen.route
    ) {
        composable(
            route = Screen.ProfileScreen.route
        ) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                state = viewModel.state.value,
                displayName = viewModel.displayName.invoke(),
                photoUrl = viewModel.photoUrl.invoke(),
                eventFlowState = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
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