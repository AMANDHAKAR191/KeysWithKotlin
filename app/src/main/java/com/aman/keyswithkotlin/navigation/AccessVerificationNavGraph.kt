package com.aman.keyswithkotlin.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keyswithkotlin.access_verification.presentation.accessVerification.AccessVerificationScreen
import com.aman.keyswithkotlin.access_verification.presentation.accessVerification.AccessVerificationViewModel

fun NavGraphBuilder.accessVerificationNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.AccessVerificationScreen.route,
        route = Graph.ACCESS_VERIFICATION
    ) {
        composable(route = Screen.AccessVerificationScreen.route) {
            val viewModel: AccessVerificationViewModel = hiltViewModel()
            AccessVerificationScreen(
                _authorizationCode = viewModel.authorizationCode,
                eventFlowState = viewModel.eventFlow,
                onEvent = viewModel::onEvent,
                navigateToProfileScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                })
        }
    }

}