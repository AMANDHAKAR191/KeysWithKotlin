package com.aman.keyswithkotlin.auth.presentation.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.core.util.Response.Failure
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import com.aman.keyswithkotlin.presentation.CustomCircularProgressBar

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
            print(signOutResponse.e)
        }
    }
}