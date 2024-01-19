package com.aman.keys.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aman.keys.auth.presentation.auth.AuthScreen
import com.aman.keys.auth.presentation.auth.AuthViewModel
import com.google.firebase.auth.GoogleAuthProvider

fun NavGraphBuilder.authNavGraph(
    navController: NavController
) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = Screen.AuthScreen.route
    ) {
        composable(
            route = Screen.AuthScreen.route
        ) {
            val viewModel: AuthViewModel = hiltViewModel()
            AuthScreen(
                oneTapSignInResponse = viewModel.oneTapSignInResponse,
                signInWithGoogleResponse = viewModel.signInWithGoogleResponse,
                oneTapSignInWithGoogle = {
                    viewModel.oneTapSignInWithGoogle()
                },
                onSignInWithFirebaseGoogleAccount = { result ->
                    val credentials =
                        viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                    viewModel.signInWithFirebaseGoogleAccount(googleCredentials)
                },
                navigateToProfileScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.PROFILE)
                },
                navigateToPasswordScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.HOME)
                }
            )
        }
    }

}