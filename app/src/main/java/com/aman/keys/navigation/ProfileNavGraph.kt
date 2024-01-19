package com.aman.keys.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keys.auth.presentation.profile.ProfileScreen
import com.aman.keys.auth.presentation.profile.ProfileViewModel

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
                _state = viewModel.state,
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
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }

}