package com.aman.keyswithkotlin.passwords.presentation.password_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharedPasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.componants.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    viewModel: PasswordViewModel = hiltViewModel(),
    sharedPasswordViewModel: ShareGeneratedPasswordViewModel,
    navigateToAddEditPasswordScreen: () -> Unit,
    navigateToGeneratePasswordScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    val state = viewModel.state.value
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var multiFloatingState by remember { mutableStateOf(MultiFloatingState.Collapsed) }
    val items = listOf(
        MinFabItem(
            icon = Icons.Default.Person,
            label = "Profile",
            identifier = Identifier.Profile.name
        ),
        MinFabItem(
            icon = Icons.Default.Password,
            label = "Generate Password",
            identifier = Identifier.GeneratePassword.name
        ),
        MinFabItem(
            icon = Icons.Default.Create,
            label = "Add Password",
            identifier = Identifier.AddEditPassword.name
        )
    )

    val openDialog = remember { mutableStateOf(false) }
    val itemToDelete = remember { mutableStateOf<Password?>(null) }
    val itemToView = remember { mutableStateOf<Password?>(null) }

    var viewPassword by remember { mutableStateOf(false) }

    if (openDialog.value) {  //ask confirmation from user to delete the expanse
        AlertDialog(
            title = { Text(text = "Alert") },
            text = { Text(text = "This password will deleted permanently. Do you still want to delete?") },
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                Text(
                    text = "Yes,delete",
                    modifier = Modifier.clickable {
                        itemToDelete.value?.let {
                            viewModel.onEvent(PasswordEvent.DeletePassword(it))
                        }
                        openDialog.value = false
                    })
            },
            dismissButton = {
                Text(text = "No",
                    modifier = Modifier.clickable {
                        println("dismissButton")
                        openDialog.value = false
                    })
            }
        )
    }

    //for showing the snackBar
    LaunchedEffect(key1 = viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddEditPasswordViewModel.UiEvent.ShowSnackBar -> {
                    val result = snackBarHostState.showSnackbar(
                        message =  event.message,
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(PasswordEvent.RestorePassword(itemToDelete.value!!))
                    }
                }

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(title = "Keys",
                onClickActionButton = {
                    navigateToProfileScreen()
                }
            )
        },
        floatingActionButton = {
            MultiFloatingButton(
                multiFloatingState = multiFloatingState,
                onMultiFabStateChange = {
                    multiFloatingState = it
                },
                item = items,
                onMinFabItemClick = {minFabItem ->
                    when(minFabItem.identifier){
                        Identifier.AddEditPassword.name->{
                            navigateToAddEditPasswordScreen()
                        }
                        Identifier.GeneratePassword.name->{
                            navigateToGeneratePasswordScreen()
                        }

                        Identifier.Profile.name -> {
                            navigateToProfileScreen()
                        }

                    }
                }
            )
        },
        bottomBar = {
        },
        content = { innerPadding ->
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    CircularProgressIndicator()
                }
            }
            Column(modifier = Modifier.padding(innerPadding)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.passwords) { password ->
                        PasswordItem(
                            password = password,
                            onItemClick = {
                                itemToView.value = password
                                viewPassword = true
                            },
                            onDeleteClick = {
                                itemToDelete.value = password
                                openDialog.value = true
                            }
                        )
                    }
                }
            }
            if (viewPassword) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            viewPassword = false
                        }
                        .background(Color(0x80000000)),
                    contentAlignment = Center
                ) {
                    ViewPasswordScreen(
                        itemToView.value!!,
                        onCloseButtonClick = {
                            viewPassword = false
                        },
                        onEditButtonClick = {
                            sharedPasswordViewModel.onEvent(SharedPasswordEvent.onEditItem(itemToView.value!!))
                            navigateToAddEditPasswordScreen()
                        })
                }
            }
        }
    )
}