package com.aman.keyswithkotlin.passwords.presentation.password_screen

import UIEvents
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.chats.presentation.BottomSheetSwipeUp
import com.aman.keyswithkotlin.chats.presentation.SharedChatEvent
import com.aman.keyswithkotlin.core.Constants.ENTER_DURATION
import com.aman.keyswithkotlin.core.Constants.EXIT_DURATION
import com.aman.keyswithkotlin.navigation.EnterAnimation
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharedPasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.componants.ExpendableFloatingButton
import com.aman.keyswithkotlin.passwords.presentation.componants.Identifier
import com.aman.keyswithkotlin.passwords.presentation.componants.MinFabItem
import com.aman.keyswithkotlin.passwords.presentation.componants.MultiFloatingState
import com.aman.keyswithkotlin.passwords.presentation.componants.PasswordItem
import com.aman.keyswithkotlin.passwords.presentation.componants.SearchedPasswordItem
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import com.aman.keyswithkotlin.passwords.presentation.componants.ViewPasswordScreen
import com.aman.keyswithkotlin.presentation.CustomCircularProgressBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PasswordScreen(
    state: PasswordState,
    eventFlowState: SharedFlow<UIEvents>,
    searchedPasswordState: State<List<Password>?>,
    onEvent: (PasswordEvent) -> Unit,
    onSharedPasswordEvent: (SharedPasswordEvent) -> Unit,
    onSharedChatEvent: (SharedChatEvent) -> Unit,
    navigateToAddEditPasswordScreen: () -> Unit,
    navigateToGeneratePasswordScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToChatUserListScreen: () -> Unit,
    navigateToAccessVerificationScreen: () -> Unit,
    closeApp: () -> Unit,
    bottomBar: @Composable (() -> Unit)
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val searchedPasswords = searchedPasswordState.value
    val snackBarHostState = remember { SnackbarHostState() }
    var multiFloatingState by remember { mutableStateOf(MultiFloatingState.Collapsed) }
    val items = listOf(
        MinFabItem(
            icon = Icons.Default.Create,
            label = "Add Password",
            identifier = Identifier.AddEditPassword.name
        ),
        MinFabItem(
            icon = Icons.Default.Password,
            label = "Generate Password",
            identifier = Identifier.GeneratePassword.name
        ),
        MinFabItem(
            icon = Icons.Default.Person,
            label = "Profile",
            identifier = Identifier.Profile.name
        )
    )

    val itemToView = remember { mutableStateOf<Password?>(null) }
    var searchtext = remember { mutableStateOf("") }
    var isSearchBarActive by remember { mutableStateOf(false) }
    var viewPassword by remember { mutableStateOf(false) }
    var isAlertDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {
                is UIEvents.ShowSnackBar -> {
                    val result = snackBarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionButtonLabel,
                        withDismissAction = event.showActionButton,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onEvent(PasswordEvent.RestorePassword)
                    }
                }

                is UIEvents.ShowAlertDialog -> {
                    println("test: check")
                    isAlertDialogVisible = true
                }

                is UIEvents.HideAlertDialog -> {
                    isAlertDialogVisible = false
                }

                else -> {}
            }
        }

    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(ENTER_DURATION.toLong())  // This delay ensures that isVisible is set to true after the initial composition
        isVisible = true
    }


    // Define a separate lambda for handling back navigation
    val handleNavigation: (String) -> Unit = {identifier->
        isVisible = false
        scope.launch {
            delay(ENTER_DURATION.toLong()) // Adjust this to match your animation duration
            when (identifier) {
                Identifier.AddEditPassword.name -> {
                    navigateToAddEditPasswordScreen()
                }

                Identifier.GeneratePassword.name -> {
                    navigateToGeneratePasswordScreen()
                }

                Identifier.Profile.name -> {
                    navigateToProfileScreen()
                }

            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .testTag("RootNode")
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(title = "Keys",
                    onClickActionButton = {
                        handleNavigation(Identifier.Profile.toString())
                    }
                )
            },
            floatingActionButton = {
                ExpendableFloatingButton(
                    multiFloatingState = multiFloatingState,
                    onMultiFabStateChange = {
                        multiFloatingState = it
                    },
                    item = items,
                    onMinFabItemClick = { minFabItem ->
                        handleNavigation(minFabItem.identifier)
                    }
                )
            },
            bottomBar = {
                bottomBar()
            },
            content = { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    color = Color.Black,
                    content = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            DockedSearchBar(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                query = searchtext.value,
                                onQueryChange = {
                                    onEvent(PasswordEvent.OnSearchTextChange(it))
                                    searchtext.value = it
                                },
                                onSearch = {},
                                colors = SearchBarDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                placeholder = { Text(text = "Search") },
                                active = isSearchBarActive,
                                onActiveChange = {isSearchBarActive = it},
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
                                },
                                content = {
                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        searchedPasswords?.let {searchedPasswords->
                                            items(searchedPasswords.take(3)) { password ->
                                                SearchedPasswordItem(
                                                    password = password,
                                                    onItemClick = {
                                                        isSearchBarActive = false
                                                        itemToView.value = password
                                                        viewPassword = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 65.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                                    )
                                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                            ) {
                                BottomSheetSwipeUp(
                                    modifier = Modifier
                                        .align(TopCenter)
                                        .padding(top = 15.dp)
                                )
                                if (state.passwords.isEmpty()) {
                                    Text(
                                        text = "No Password  Saved",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .testTag("NoPasswordHelperText")
                                    )
                                } else {
                                    LazyColumn(modifier = Modifier.padding(top = 30.dp)) {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .width(50.dp)
                                                    .padding(start = 10.dp),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(text = "Recently used passwords", textAlign = TextAlign.Start)
                                            }
                                        }
                                        items(state.recentlyUsedPasswords.take(3)) { password ->
                                            PasswordItem(
                                                password = password,
                                                onItemClick = {
                                                    itemToView.value = password
                                                    viewPassword = true
                                                },
                                                onDeleteClick = {
                                                    onEvent(PasswordEvent.DeletePassword(password = password))
                                                }
                                            )
                                        }
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .width(50.dp)
                                                    .padding(start = 10.dp),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(text = "All passwords")
                                            }
                                        }

                                        items(state.passwords) { password ->
                                            PasswordItem(
                                                password = password,
                                                onItemClick = {
                                                    itemToView.value = password
                                                    viewPassword = true
                                                },
                                                onDeleteClick = {
                                                    onEvent(
                                                        PasswordEvent.UpdateLastUsedPasswordTimeStamp(
                                                            password = password
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .padding(top = 10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(text = "This is end of passwords")
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                )

                if (multiFloatingState == MultiFloatingState.Expended) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { multiFloatingState = MultiFloatingState.Collapsed }
                    )
                }


                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                        CustomCircularProgressBar()
                    }
                }

                //for Access Alert
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
                            Text(
                                text = "You device is not Authorized. If you are not Authorized user then you can't use this account.",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                navigateToAccessVerificationScreen()
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
                                    closeApp()
                                }
                            ) {
                                Text("Ok")
                            }
                        },
                        modifier = Modifier.testTag("AuthorizationAlertDialog")
                    )
                }
            }
        )
        // Sheet content
        if (viewPassword) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewPassword = false
                },
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                ViewPasswordScreen(
                    itemToView.value!!,
                    onCloseButtonClick = {
                        viewPassword = false
                    },
                    onEditButtonClick = {
                        onSharedPasswordEvent(SharedPasswordEvent.onEditItem(itemToView.value!!))
                        navigateToAddEditPasswordScreen()
                    },
                    onShareButtonClick = { password ->
                        onSharedChatEvent(SharedChatEvent.SharePasswordItem(password))
                        navigateToChatUserListScreen()
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun preview() {
    PasswordScreen(
        state = PasswordState(),
        eventFlowState = MutableSharedFlow(), // Using MutableSharedFlow as dummy value
        searchedPasswordState = remember {
            mutableStateOf(
                listOf(
                    Password(),
                    Password()
                )
            )
        }, // Using a list of dummy passwords
        onEvent = {},
        onSharedPasswordEvent = {},
        onSharedChatEvent = {},
        navigateToAddEditPasswordScreen = {},
        navigateToGeneratePasswordScreen = {},
        navigateToProfileScreen = {},
        navigateToChatUserListScreen = {},
        navigateToAccessVerificationScreen = {},
        closeApp = {}
    ) {}
}
