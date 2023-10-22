package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import UIEvents
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.core.Constants.EXIT_DURATION
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.components.CustomTransparentTextField
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.components.TransparentHintTextField
import com.aman.keyswithkotlin.passwords.domain.model.Password
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPasswordScreen(
    state: AddEditPasswordState,
    eventFlow: SharedFlow<UIEvents>,
    onEvent: (PasswordEvent) -> Unit,
    onSharedPasswordEvent: (SharedPasswordEvent) -> Unit,
    generatedPassword: String? = "",
    passwordItem: Password?,
    navigateBack: () -> Unit,
    navigateToGeneratePasswordScreen: () -> Unit
) {

    val focusState = remember {
        mutableStateOf(false)
    }
    //if generatedPassword is not null then we are coming from GeneratePasswordScreen
    if (!generatedPassword.equals("")) {
        generatedPassword?.let {
            onEvent(PasswordEvent.EnteredPassword(it))
        }
    }

    passwordItem?.let {
        onEvent(PasswordEvent.EnteredUsername(passwordItem.userName))
        onEvent(PasswordEvent.EnteredPassword(passwordItem.password))
        onEvent(PasswordEvent.EnteredWebsiteName(passwordItem.websiteName))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    //for showing the snackBar
    LaunchedEffect(key1 = eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is UIEvents.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is UIEvents.SavePassword -> {
                    navigateBack()
                }

                else -> {}
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Add Password") },
                navigationIcon = {
                    IconButton(onClick = {
                        onSharedPasswordEvent(SharedPasswordEvent.resetViewmodel)
                        navigateBack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }, content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CustomTransparentTextField(
                    text = state.username,
                    label = "Username",
                    hint = "Enter userName",
                    onValueChange = {
                        onEvent(PasswordEvent.EnteredUsername(it))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    showIndicator = false,

                    )
                CustomTransparentTextField(
                    text = state.password,
                    label = "Password",
                    hint = "Enter Password",
                    onValueChange = {
                        onEvent(PasswordEvent.EnteredPassword(it))
                    },
                    showIndicator = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    trailingIcon = {
                        if (focusState.value) {
                            TextButton(onClick = {
                                navigateToGeneratePasswordScreen()
                            }) {
                                Text(text = "Generate")
                            }
                        }
                    },
                    onFocusChange = {
                        focusState.value = it.isFocused
                    }
                )
                CustomTransparentTextField(
                    text = state.websiteName,
                    label = "Website name",
                    hint = "Enter website name",
                    onValueChange = {
                        onEvent(PasswordEvent.EnteredWebsiteName(it))
                    },
                    showIndicator = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                CustomTransparentTextField(
                    text = state.username,
                    label = "Website Link (optional)",
                    hint = "Enter userName",
                    onValueChange = {
//                        onEvent(PasswordEvent.EnteredUsername(it))
                    },
                    showIndicator = false,
                )
                Button(
                    onClick = {
                        onEvent(PasswordEvent.SavePassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 100.dp)
                        .padding(top = 20.dp)
                ) {
                    Text(text = "Save")
                }
            }
        })
}


@Preview
@Composable
fun Preview() {
    val state = AddEditPasswordState() // Provide the desired state object
    val eventFlow =
        remember { MutableSharedFlow<UIEvents>() } // Create an instance of MutableSharedFlow
    val onEvent: (PasswordEvent) -> Unit = {} // Provide an empty lambda function for onEvent
    AddEditPasswordScreen(
        state = state,
        eventFlow = eventFlow,
        onEvent = onEvent,
        onSharedPasswordEvent = {},
        passwordItem = Password(),
        navigateBack = { /*TODO*/ },
        navigateToGeneratePasswordScreen = {})
}