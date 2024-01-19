package com.aman.keys.auth.presentation.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.aman.keys.auth.domain.repository.OneTapSignInResponse
import com.aman.keys.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keys.auth.presentation.auth.components.AuthContent
import com.aman.keys.auth.presentation.auth.components.OneTapSignIn
import com.aman.keys.auth.presentation.auth.components.SignInWithGoogle
import com.aman.keys.core.util.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AuthScreen(
    oneTapSignInResponse:OneTapSignInResponse,
    signInWithGoogleResponse:SignInWithGoogleResponse,
    oneTapSignInWithGoogle:()->Unit,
    onSignInWithFirebaseGoogleAccount:(ActivityResult)->Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToPasswordScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Keys"
                    )
                }
            )
        },
        content = { padding ->
            AuthContent(
                padding = padding,
                oneTapSignIn = {
                    oneTapSignInWithGoogle()
                }
            )
        }
    )

    val launcher = rememberLauncherForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                onSignInWithFirebaseGoogleAccount(result)
            } catch (it: ApiException) {
                print(it)
            }
        }
    }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    OneTapSignIn(
        oneTapSignInResponse = oneTapSignInResponse,
        launch = {
            launch(it)
        }
    )

    SignInWithGoogle(
        signInWithGoogleResponse = signInWithGoogleResponse,
        navigateToHomeScreen = { signedIn ->
            if (signedIn) {
                navigateToPasswordScreen()
            }
        }
    )
}

@Preview
@Composable
fun preview(){
    AuthScreen(
        oneTapSignInResponse = Response.Success(null),
        signInWithGoogleResponse = Response.Success(status = true),
        oneTapSignInWithGoogle = { /*TODO*/ },
        onSignInWithFirebaseGoogleAccount = {},
        navigateToProfileScreen = { /*TODO*/ }) {

    }
}