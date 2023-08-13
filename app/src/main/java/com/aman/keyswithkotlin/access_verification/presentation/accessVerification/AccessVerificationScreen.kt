package com.aman.keyswithkotlin.access_verification.presentation.accessVerification

import UIEvents
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AccessVerificationScreen(
    eventFlowState: SharedFlow<UIEvents>,
    navigateToProfileScreen:()->Unit,
) {


    var isAlertDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {
                is UIEvents.ShowAlertDialog -> {
                    isAlertDialogVisible = true
                }

                is UIEvents.NavigateToNextScreen->{
                    navigateToProfileScreen()
                }
                is UIEvents.HideAlertDialog->{
                    navigateToProfileScreen()
                }

                else -> {}
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(title = "Keys",
                onClickActionButton = {

                }
            )
        },
        content = { innerPadding ->
            if (isAlertDialogVisible){
                println("check2::")
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
                        Text(text = "You device is not Authorized", color = MaterialTheme.colorScheme.error)
                    },
                    confirmButton = {
                        Button(onClick = { navigateToProfileScreen() }) {
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
                                isAlertDialogVisible = false
                            }
                        ) {
                            Text("Ok")
                        }
                    }
                )
            }
        }
    )
}