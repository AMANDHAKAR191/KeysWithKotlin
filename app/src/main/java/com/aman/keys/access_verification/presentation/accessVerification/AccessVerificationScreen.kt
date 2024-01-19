package com.aman.keys.access_verification.presentation.accessVerification

import UIEvents
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aman.keys.passwords.presentation.componants.TopBar
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AccessVerificationScreen(
    _authorizationCode: StateFlow<Int>,
    eventFlowState: SharedFlow<UIEvents>,
    navigateToProfileScreen: () -> Unit,
    onEvent: (AccessVerificationEvent) -> Unit
) {

    val authorizationCode = _authorizationCode.collectAsState()
    var isAlertDialogVisible by remember { mutableStateOf(false) }
    var isAuthorizationAlertDialogVisible by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {
                is UIEvents.ShowAlertDialog -> {
                    isAlertDialogVisible = true
                }

                is UIEvents.NavigateToNextScreen -> {
                    navigateToProfileScreen()
                }

                is UIEvents.HideAlertDialog -> {
                    navigateToProfileScreen()
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                title = "Keys"
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center){
                if (isAlertDialogVisible) {
                    AlertDialog(
                        onDismissRequest = {
                            // Dismiss the dialog when the user clicks outside the dialog or on the back
                            // button. If you want to disable that functionality, simply use an empty
                            // onDismissRequest.
                        },
                        title = {
                            Text(text = "Warning!", color = MaterialTheme.colorScheme.error)
                        },
                        text = {
                            Column {
                                Text(
                                    text = "You device is not Authorized",
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "${authorizationCode.value}",
                                    color = Color.Black
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
//                            navigateToProfileScreen()
                                onEvent(AccessVerificationEvent.AskAccessPermission)
                            }) {
                                Text("Ask Permission")
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
                                    onEvent(AccessVerificationEvent.CancelAuthorizationAccessProcess)
                                    isAlertDialogVisible = false
                                }
                            ) {
                                Text("Cancel request")
                            }
                        }
                    )
                }
                if (isAuthorizationAlertDialogVisible) {
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
                                onEvent(AccessVerificationEvent.GrantAccessPermission)
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
                                    navigateToProfileScreen()
                                    isAuthorizationAlertDialogVisible = false
                                }
                            ) {
                                Text("Decline")
                            }
                        }
                    )
                }
            }

        }
    )
}