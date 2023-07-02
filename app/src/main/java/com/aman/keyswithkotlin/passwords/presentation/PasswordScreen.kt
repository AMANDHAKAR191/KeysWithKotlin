package com.aman.keyswithkotlin.passwords.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.componants.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    viewModel: PasswordViewModel = hiltViewModel(),
    navigateToAddEditPasswordScreen: () -> Unit
) {
    val state = viewModel.state.value
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val openDialog = remember { mutableStateOf(false) }
    val itemToDelete = remember { mutableStateOf<Password?>(null) }
    if (openDialog.value) {  //ask confirmation from user to delete the expanse
        AlertDialog(
            title = { Text(text = "Alert") },
            text = { Text(text = "This password will deleted permanently. Do you still want to delete?") },
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                Text(text = "Yes,delete",
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
                onClickBackIcon = {

                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigateToAddEditPasswordScreen()
            }) {
                Icon(Icons.Default.Create, contentDescription = "")
            }
        },
        bottomBar = {
        }
    ) { innerPadding ->

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
                        onItemClick = {},
                        onDeleteClick = {
                            itemToDelete.value = password
                            openDialog.value = true
                        }
                    )
                }
            }
        }
    }
}