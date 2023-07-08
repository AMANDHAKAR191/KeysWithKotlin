package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPasswordScreen(
    state: AddEditPasswordState,
    eventFlow:SharedFlow<AddEditPasswordViewModel.UiEvent>,
    onEvent:(PasswordEvent)->Unit,
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel,
    navigateToPasswordScreen: () -> Unit,
    navigateToGeneratePasswordScreen: () -> Unit
) {

    val generatedPassword = sharedPasswordViewModel.generatedPassword.value.generatedPassword
    val passwordItem = sharedPasswordViewModel.itemToEdit.value.passwordItem

    val focusState = remember {
        mutableStateOf(false)
    }
    //if generatedPassword is not null then we are coming from GeneratePasswordScreen
    println("generatedPassword: ${generatedPassword}")
    if (generatedPassword != "") {
        onEvent(PasswordEvent.EnteredPassword(generatedPassword))
    }

    //if
    println("generatedPassword: ${generatedPassword}")
    if (passwordItem != null) {
        onEvent(PasswordEvent.EnteredUsername(passwordItem.userName))
        onEvent(PasswordEvent.EnteredPassword(passwordItem.password))
        onEvent(PasswordEvent.EnteredWebsiteName(passwordItem.websiteName))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    //for showing the snackBar
    LaunchedEffect(key1 = eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is AddEditPasswordViewModel.UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is AddEditPasswordViewModel.UiEvent.savePassword -> {
                    navigateToPasswordScreen()
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
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            sharedPasswordViewModel.onEvent(SharedPasswordEvent.resetViewmodel)
                            navigateToPasswordScreen()
                        }
                    )
                }
            )
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                CustomTextField(
                    text = state.username,
                    label = "Username",
                    onValueChange = { onEvent(PasswordEvent.EnteredUsername(it)) },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                CustomTextField(
                    text = state.password,
                    label = "Password",
                    onValueChange = { onEvent(PasswordEvent.EnteredPassword(it)) },
                    enabled = true,
                    singleLine = true,
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
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                CustomTextField(
                    text = state.websiteName,
                    label = "Website Name",
                    onValueChange = { onEvent(PasswordEvent.EnteredWebsiteName(it)) },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                CustomTextField(
                    text = state.websiteLink,
                    label = "Website Link (optional)",
                    onValueChange = { onEvent(PasswordEvent.EnteredWebsiteName(it)) },
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Button(onClick = {
                    onEvent(PasswordEvent.SavePassword)
                }) {
                    Text(text = "Save")
                }
            }
        })


}