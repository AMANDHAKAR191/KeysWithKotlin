package com.aman.keyswithkotlin.passwords.presentation.add_edit_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPasswordScreen(
    viewModel: AddEditPasswordViewModel = hiltViewModel(),
    navigateToPasswordScreen: () -> Unit
) {
    val userName = viewModel.userName.value
    val password = viewModel.password.value
    val websiteName = viewModel.websiteName.value
    val websiteLink = viewModel.websiteLink.value
    val eventFlow = viewModel.eventFlow

    val focusState = remember {
        mutableStateOf(false)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    //for showing the snackBar
    LaunchedEffect(key1 = viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
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

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Add Password") }
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CustomTextField(
                text = userName.text,
                label = userName.hint,
                onValueChange = { viewModel.onEvent(PasswordEvent.EnteredUsername(it)) },
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            CustomTextField(
                text = password.text,
                label = password.hint,
                onValueChange = { viewModel.onEvent(PasswordEvent.EnteredPassword(it)) },
                enabled = true,
                singleLine = true,
                trailingIcon = {
                    if (focusState.value){
                        Button(onClick = { /*TODO*/ }) {
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
                text = websiteName.text,
                label = websiteName.hint,
                onValueChange = { viewModel.onEvent(PasswordEvent.EnteredWebsiteName(it)) },
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            CustomTextField(
                text = websiteLink.text,
                label = websiteLink.hint,
                onValueChange = { viewModel.onEvent(PasswordEvent.EnteredWebsiteName(it)) },
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
            Button(onClick = {
                viewModel.onEvent(PasswordEvent.SavePassword)
            }) {
                Text(text = "Save")
            }
        }
    }


}