package com.aman.keyswithkotlin.auth.presentation.auth.components

import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.auth.domain.repository.OneTapSignInResponse
import com.aman.keyswithkotlin.core.util.Response.Failure
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import com.aman.keyswithkotlin.auth.presentation.auth.AuthViewModel
import com.aman.keyswithkotlin.core.components.ProgressBar
import com.aman.keyswithkotlin.core.components.ShowInfoToUser
import com.aman.keyswithkotlin.presentation.CustomCircularProgressBar
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
        is Loading -> CustomCircularProgressBar(showStatus = true, status = "Authenticating account with google...")
        is Success<BeginSignInResult,*> -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }

        is Failure -> {
            println("check456456456")
            showErrorDialog.value = true
            if (showErrorDialog.value){
                ShowInfoToUser(showDialog = true, title = "Error", message = oneTapSignInResponse.e.message){
                    showErrorDialog.value = false
                }
            }
//            CustomCircularProgressBar(showStatus = true, status = "Error: ${oneTapSignInResponse.e.message}")
            LaunchedEffect(Unit) {
                print(oneTapSignInResponse.e)
            }
        }

    }
}