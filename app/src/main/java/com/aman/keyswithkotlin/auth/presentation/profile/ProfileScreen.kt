package com.aman.keyswithkotlin.auth.presentation.profile

import UIEvents
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.access_verification.presentation.accessVerification.AccessVerificationEvent
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.auth.presentation.AuthEvent
import com.aman.keyswithkotlin.auth.presentation.auth.AuthScreen
import com.aman.keyswithkotlin.auth.presentation.profile.components.ProfileContent
import com.aman.keyswithkotlin.auth.presentation.profile.components.ProfileTopBar
import com.aman.keyswithkotlin.auth.presentation.profile.components.RevokeAccess
import com.aman.keyswithkotlin.auth.presentation.profile.components.SignOut
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.Constants
import com.aman.keyswithkotlin.core.Constants.REVOKE_ACCESS_MESSAGE
import com.aman.keyswithkotlin.core.Constants.SIGN_OUT
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.DeviceType
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.navigation.EnterAnimationForFAB
import com.aman.keyswithkotlin.navigation.EnterAnimationForProfileScreen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    displayName:String,
    photoUrl:String,
    onEvent: (AuthEvent) -> Unit,
    eventFlowState: SharedFlow<UIEvents>,
    revokeAccessResponse:RevokeAccessResponse,
    signOutResponse:SignOutResponse,
    onSignOut:()->Unit,
    onRevokeAccess:()->Unit,
    navigateToAuthScreen: () -> Unit,
    navigateToPasswordScreen: () -> Unit,
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val coroutineScope = rememberCoroutineScope()
    var isAuthorizationAlertDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {

                is UIEvents.NavigateToNextScreen -> {
                    isAuthorizationAlertDialogVisible= false
                }

                is UIEvents.ShowAuthorizationAlertDialog -> {
                    isAuthorizationAlertDialogVisible = true
                }

                is UIEvents.HideAuthorizationAlertDialog -> {
                    isAuthorizationAlertDialogVisible = false
                }

                else -> {}
            }
        }

    }

    var isVisible by remember { mutableStateOf(true) }

    // Define a separate lambda for handling back navigation
    val handleBackNavigation: (String) -> Unit = {
        isVisible = false
        coroutineScope.launch {
            delay(Constants.EXIT_DURATION.toLong()) // Adjust this to match your animation duration
            when(it){
                "AuthScreen"->{navigateToAuthScreen()}
                "PasswordScreen"->{navigateToPasswordScreen()}
            }
        }
    }

    EnterAnimationForProfileScreen(visible = isVisible) {
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
                        handleBackNavigation("PasswordScreen")
                    }
                )
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
                        val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
                        var isCurrentUserPrimary by remember { mutableStateOf(false) }
                        //to check is current device is primary
                        for (deviceData in state.loggedInDeviceList){
                            if (deviceData.deviceId == deviceInfo.getDeviceId()){
                                if(deviceData.deviceType == DeviceType.Primary.toString()) {
                                    isCurrentUserPrimary = true
                                    break
                                }
                            }
                        }
                        LazyColumn{
                            items(state.loggedInDeviceList){deviceData->
                                Column {
                                    Text(text = "Device ID: ${deviceData.deviceId}")
                                    Text(text = "Device Name: ${deviceData.deviceName}")
                                    Text(text = "Device Type: ${deviceData.deviceType}")
                                    Text(text = "Device Authorization: ${deviceData.authorization}")
                                    Text(text = "Last login time: ${deviceData.lastLoginTimeStamp}")
                                    //if current device is primary device then show remove access for secondary devices
                                    if (isCurrentUserPrimary){
                                        if (deviceData.deviceType == DeviceType.Secondary.toString()){
                                            Button(onClick = {
                                                if (deviceData.authorization == Authorization.Authorized.toString()){
                                                    println("deviceName: ${deviceData.deviceName} authorization: ${deviceData.authorization}")
                                                    onEvent(AuthEvent.RemoveAuthorizationAccess(deviceData.deviceId!!))
                                                }else{
                                                    println("deviceName: ${deviceData.deviceName} authorization: ${deviceData.authorization}")
                                                    onEvent(AuthEvent.GiveAuthorizationAccess(deviceData.deviceId!!))
                                                }
                                            }) {
                                                if (deviceData.authorization == Authorization.Authorized.toString()){
                                                    Text(text = "Remove Access")
                                                }else{
                                                    Text(text = "Give Access")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isAuthorizationAlertDialogVisible) {
                    println("check2::")
                    AlertDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when the user clicks outside the dialog or on the back
                            // button. If you want to disable that functionality, simply use an empty
                            // onDismissRequest.
                        },
                        title = {
                            Text(text = "Grant Permission!", color = MaterialTheme.colorScheme.error)
                        },
                        text = {
                            Text(
                                text = "Device is requesting for Login permission",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                onEvent(AuthEvent.GrantAccessPermission)
                            }) {
                                Text("Grant Permission")
                            }
//                        TextButton(
//                            onClick = {
//
//                            }
//                        ) {
//                            Text("Ask Permission")
//                        }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    onEvent(AuthEvent.DeclineAuthorizationAccessProcess)
                                }
                            ) {
                                Text("Decline")
                            }
                        }
                    )
                }
            }
        )
    }

    SignOut(
        signOutResponse = signOutResponse,
        navigateToAuthScreen = { signedOut ->
            if (signedOut) {
                handleBackNavigation("AuthScreen")
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
                handleBackNavigation("AuthScreen")
            }
        },
        showSnackBar = {
            showSnackBar()
        }
    )
}

//@Preview
//@Composable
//fun preview(){
//    ProfileScreen(
//        state = ProfileState(),
//        displayName = "Aman dhaker",
//        photoUrl = "",
//        onEvent = {},
//        signOutResponse = Response.Success(false),
//        revokeAccessResponse = Response.Success(false),
//        onSignOut = { /*TODO*/ },
//        onRevokeAccess = { /*TODO*/ },
//        navigateToAuthScreen = { /*TODO*/ },
//        navigateToPasswordScreen = { /*TODO*/ }) {
//
//    }
//}