package com.aman.keys.auth.presentation.auth.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.aman.keys.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keys.core.components.ShowInfoToUser
import com.aman.keys.core.util.Response.Failure
import com.aman.keys.core.util.Response.Loading
import com.aman.keys.core.util.Response.Success
import com.aman.keys.presentation.CustomCircularProgressBar

@Composable
fun SignInWithGoogle(
    signInWithGoogleResponse: SignInWithGoogleResponse,
    navigateToHomeScreen: (signedIn: Boolean) -> Unit
) {
    when (signInWithGoogleResponse) {
        is Loading -> CustomCircularProgressBar(
            showStatus = true,
            status = "Authenticating account with firebase..."
        )

        is Success -> signInWithGoogleResponse.status?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }

        is Failure -> {
            var showErrorDialog = remember {
                mutableStateOf(false)
            }
            ShowInfoToUser(
                showDialog = showErrorDialog.value,
                title = "Error",
                message = signInWithGoogleResponse.e.message,
                onRetry = {
                    showErrorDialog.value = false
                }
            )
            LaunchedEffect(Unit) {
                print(signInWithGoogleResponse.e)
            }
        }
    }
}