package com.aman.keys.auth.presentation.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.aman.keys.auth.domain.repository.SignOutResponse
import com.aman.keys.core.util.Response.Failure
import com.aman.keys.core.util.Response.Loading
import com.aman.keys.core.util.Response.Success
import com.aman.keys.presentation.CustomCircularProgressBar

@Composable
fun SignOut(
    signOutResponse: SignOutResponse,
    navigateToAuthScreen: (signedOut: Boolean) -> Unit
) {
    when (signOutResponse) {
        is Loading -> CustomCircularProgressBar()
        is Success -> signOutResponse.data?.let { signedOut ->
            LaunchedEffect(signedOut) {
                navigateToAuthScreen(signedOut)
            }
        }

        is Failure -> LaunchedEffect(Unit) {
        }
    }
}