package com.aman.keyswithkotlin.passwords.presentation.password_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharedPasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.componants.*
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    state: PasswordState,
    eventFlowState:SharedFlow<AddEditPasswordViewModel.UiEvent>,
    searchedPasswordState:State<List<Password>>,
    onEvent:(PasswordEvent)->Unit,
    onSharedPasswordEvent:(SharedPasswordEvent)->Unit,
    navigateToAddEditPasswordScreen: () -> Unit,
    navigateToGeneratePasswordScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    bottomBar:@Composable (()->Unit)
) {
    val searchedPasswords = searchedPasswordState.value
    val snackBarHostState = remember { SnackbarHostState() }
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
    var searchtext = remember { mutableStateOf("") }
    var isSearchBarActive by remember { mutableStateOf(false) }
    var viewPassword by remember { mutableStateOf(false) }

    //ask confirmation from user to delete the expanse
    if (openDialog.value) {
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
                            onEvent(PasswordEvent.DeletePassword(it))
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
    LaunchedEffect(key1 = eventFlowState) {
        eventFlowState.collect { event ->
            when (event) {
                is AddEditPasswordViewModel.UiEvent.ShowSnackBar -> {
                    val result = snackBarHostState.showSnackbar(
                        message =  event.message,
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onEvent(PasswordEvent.RestorePassword(itemToDelete.value!!))
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
                    bottomBar()
        },
        content = { innerPadding ->
            Surface(
                shape = RoundedCornerShape(20f),
                tonalElevation = 5.dp,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.surface,
                content = {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchBar(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            query = searchtext.value,
                            onQueryChange = {
                                onEvent(PasswordEvent.OnSearchTextChange(it))
                                searchtext.value = it },
                            onSearch = {

                            },
                            active = isSearchBarActive,
                            onActiveChange = { isSearchBarActive = it },
                            colors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            placeholder = { Text(text = "Search") },
                            leadingIcon = {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            },
                            trailingIcon = {
                                if (isSearchBarActive) {
                                    IconButton(onClick = {
                                        if (searchtext.value != "") {
                                            searchtext.value = ""
                                        } else {
                                            isSearchBarActive = false
                                        }
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close")
                                    }
                                }
                            }
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(searchedPasswords) { password ->
                                    PasswordItem(
                                        password = password,
                                        onItemClick = {
                                            isSearchBarActive = false
                                            itemToView.value = password
                                            viewPassword = true
                                        },
                                        onDeleteClick = {
                                        }
                                    )
                                }
                            }
                        }
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
                }
            )


            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    CircularProgressIndicator()
                }
            }
            //for view password in separate dialog
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
                            onSharedPasswordEvent(SharedPasswordEvent.onEditItem(itemToView.value!!))
                            navigateToAddEditPasswordScreen()
                        })
                }
            }
        }
    )
}

@Preview
@Composable
fun preview(){

}