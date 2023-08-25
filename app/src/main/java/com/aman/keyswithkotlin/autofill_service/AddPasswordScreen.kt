package com.aman.keyswithkotlin.autofill_service

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.notes.presentation.add_edit_note.components.TransparentHintTextField
import com.aman.keyswithkotlin.passwords.domain.model.Password
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPasswordScreen(
) {

    val focusState = remember {
        mutableStateOf(false)
    }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var websiteName by remember { mutableStateOf("") }
    var websiteLink by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }


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
                        }
                    )
                }
            )
        }, content = {innerPadding->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TransparentHintTextField(
                    text = username,
                    label = "Username",
                    hint = "Enter userName",
                    onValueChange = {
                                    username = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    showIndicator = false,

                    )
                TransparentHintTextField(
                    text = password,
                    label = "Password",
                    hint = "Enter Password",
                    onValueChange = {
                        password = it
                    },
                    showIndicator = false,
                    trailingIcon = {
                        if (focusState.value) {
                            TextButton(onClick = {

                            }) {
                                Text(text = "Generate")
                            }
                        }
                    },
                    onFocusChange = {
                        focusState.value = it.isFocused
                    }
                )
                TransparentHintTextField(
                    text = websiteName,
                    label = "Website name",
                    hint = "Enter website name",
                    onValueChange = {
                        websiteName = it
                    },
                    showIndicator = false,
                )
                TransparentHintTextField(
                    text = username,
                    label = "Website Link (optional)",
                    hint = "Enter userName",
                    onValueChange = {
//                        onEvent(PasswordEvent.EnteredUsername(it))
                    },
                    showIndicator = false,
                )
                Button(
                    onClick = {
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