package com.aman.keys.auth.presentation.auth.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.aman.keys.auth.domain.repository.OneTapSignInResponse
import com.aman.keys.core.components.ShowInfoToUser
import com.aman.keys.core.util.Response.Failure
import com.aman.keys.core.util.Response.Loading
import com.aman.keys.core.util.Response.Success
import com.aman.keys.presentation.CustomCircularProgressBar
import com.google.android.gms.auth.api.identity.BeginSignInResult

@Composable
fun OneTapSignIn(
    oneTapSignInResponse: OneTapSignInResponse,
    launch: (result: BeginSignInResult) -> Unit
) {
    var showErrorDialog = remember {
        mutableStateOf(false)
    }

    when (oneTapSignInResponse) {
        is Loading -> CustomCircularProgressBar(
            showStatus = true,
            status = "Authenticating account with google..."
        )

        is Success<BeginSignInResult, *> -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }

        is Failure -> {
            showErrorDialog.value = true
            ShowInfoToUser(
                showDialog = showErrorDialog.value,
                title = "Error",
                message = oneTapSignInResponse.e.message,
                onRetry = {
                    showErrorDialog.value = false
                }
            )
//            CustomCircularProgressBar(showStatus = true, status = "Error: ${oneTapSignInResponse.e.message}")
            LaunchedEffect(Unit) {
                print(oneTapSignInResponse.e)
            }
        }

    }
}