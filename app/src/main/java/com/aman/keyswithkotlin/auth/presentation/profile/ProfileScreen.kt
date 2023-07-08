package com.aman.keyswithkotlin.auth.presentation.profile

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.auth.presentation.profile.components.ProfileContent
import com.aman.keyswithkotlin.auth.presentation.profile.components.ProfileTopBar
import com.aman.keyswithkotlin.auth.presentation.profile.components.RevokeAccess
import com.aman.keyswithkotlin.auth.presentation.profile.components.SignOut
import com.aman.keyswithkotlin.core.Constants.REVOKE_ACCESS_MESSAGE
import com.aman.keyswithkotlin.core.Constants.SIGN_OUT
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    displayName:String,
    photoUrl:String,
    revokeAccessResponse:RevokeAccessResponse,
    signOutResponse:SignOutResponse,
    onSignOut:()->Unit,
    onRevokeAccess:()->Unit,
    navigateToAuthScreen: () -> Unit,
    navigateToPasswordScreen: () -> Unit,
    bottomBar: @Composable (() -> Unit)
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            ProfileTopBar(
                signOut = {
                    onSignOut()
                },
                revokeAccess = {
                    onRevokeAccess()
                },
                navigateToPasswordScreen = {
                    navigateToPasswordScreen()
                }
            )
        },
        bottomBar = {
            bottomBar()
        },
        content = { padding ->
            ProfileContent(
                padding = padding,
                photoUrl = photoUrl,
                displayName = displayName
            )
        }
    )

    SignOut(
        signOutResponse = signOutResponse,
        navigateToAuthScreen = { signedOut ->
            if (signedOut) {
                navigateToAuthScreen()
            }
        }
    )

    fun showSnackBar() = coroutineScope.launch {
        val result = snackBarHostState.showSnackbar(
            message = REVOKE_ACCESS_MESSAGE,
            actionLabel = SIGN_OUT
        )
        if (result == SnackbarResult.ActionPerformed) {
            onSignOut()
        }
    }

    RevokeAccess(
        revokeAccessResponse = revokeAccessResponse,
        navigateToAuthScreen = { accessRevoked ->
            if (accessRevoked) {
                navigateToAuthScreen()
            }
        },
        showSnackBar = {
            showSnackBar()
        }
    )
}

@Preview
@Composable
fun preview(){
    ProfileScreen(
        displayName = "Aman dhaker",
        photoUrl = "",
        signOutResponse = Response.Success(false),
        revokeAccessResponse = Response.Success(false),
        onSignOut = { /*TODO*/ },
        onRevokeAccess = { /*TODO*/ },
        navigateToAuthScreen = { /*TODO*/ },
        navigateToPasswordScreen = { /*TODO*/ }) {
        
    }
}