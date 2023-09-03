package com.aman.keyswithkotlin.auth.presentation.auth.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.aman.keyswithkotlin.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keyswithkotlin.core.util.Response.Failure
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import com.aman.keyswithkotlin.core.components.ProgressBar
import com.aman.keyswithkotlin.presentation.CustomCircularProgressBar

@Composable
fun SignInWithGoogle(
    signInWithGoogleResponse:SignInWithGoogleResponse,
    navigateToHomeScreen: (signedIn: Boolean) -> Unit
) {
    when (signInWithGoogleResponse) {
        is Loading -> CustomCircularProgressBar(showStatus = true, status = "Authenticating account with firebase...")
        is Success -> signInWithGoogleResponse.status?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }

        is Failure -> LaunchedEffect(Unit) {
            print(signInWithGoogleResponse.e)
        }
    }
}