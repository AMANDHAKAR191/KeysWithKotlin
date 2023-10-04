package com.aman.keyswithkotlin.auth.presentation.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.auth.domain.repository.OneTapSignInResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keyswithkotlin.auth.presentation.auth.components.AuthContent
import com.aman.keyswithkotlin.auth.presentation.auth.components.AuthTopBar
import com.aman.keyswithkotlin.auth.presentation.auth.components.OneTapSignIn
import com.aman.keyswithkotlin.auth.presentation.auth.components.SignInWithGoogle
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.navigation.AUTH_SCREEN
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider.getCredential

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
                navigateToProfileScreen()
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