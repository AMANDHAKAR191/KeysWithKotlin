package com.aman.keyswithkotlin.auth.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.auth.presentation.profile.components.ProfileContent
import com.aman.keyswithkotlin.auth.presentation.profile.components.ProfileTopBar
import com.aman.keyswithkotlin.auth.presentation.profile.components.RevokeAccess
import com.aman.keyswithkotlin.auth.presentation.profile.components.SignOut
import com.aman.keyswithkotlin.core.Constants.REVOKE_ACCESS_MESSAGE
import com.aman.keyswithkotlin.core.Constants.SIGN_OUT
import com.aman.keyswithkotlin.core.DeviceType
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(48.dp)
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(96.dp)
                        .height(96.dp)
                )
                Text(
                    text = displayName,
                    fontSize = 24.sp
                )
                Column {
                    LazyColumn{
                        items(state.loggedInDeviceList){deviceData->
                            Column {
                                Text(text = "Device ID: ${deviceData.deviceId}")
                                Text(text = "Device Name: ${deviceData.deviceName}")
                                Text(text = "Device Type: ${deviceData.deviceType}")
                                Text(text = "Device Authorization: ${deviceData.authorization}")
                                Text(text = "Last login time: ${deviceData.lastLoginTimeStamp}")
                                if (deviceData.deviceType == DeviceType.Primary.toString()){
                                    Button(onClick = { /*TODO*/ }) {
                                        Text(text = "Remove Access")
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
        state = ProfileState(),
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