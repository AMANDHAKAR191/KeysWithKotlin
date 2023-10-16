package com.aman.keyswithkotlin.passwords.presentation.password_screen

import UIEvents
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.chats.presentation.BottomSheetSwipeUp
import com.aman.keyswithkotlin.chats.presentation.SharedChatEvent
import com.aman.keyswithkotlin.core.BiometricStatus
import com.aman.keyswithkotlin.core.Constants.ENTER_DURATION
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.PasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharedPasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.componants.ExpendableFloatingButton
import com.aman.keyswithkotlin.passwords.presentation.componants.Identifier
import com.aman.keyswithkotlin.passwords.presentation.componants.MinFabItem
import com.aman.keyswithkotlin.passwords.presentation.componants.MultiFloatingState
import com.aman.keyswithkotlin.passwords.presentation.componants.PasswordItem
import com.aman.keyswithkotlin.passwords.presentation.componants.PasswordSettingsScreen
import com.aman.keyswithkotlin.passwords.presentation.componants.SearchedPasswordItem
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import com.aman.keyswithkotlin.passwords.presentation.componants.ViewPasswordScreen
import com.aman.keyswithkotlin.presentation.CustomCircularProgressBar
import com.aman.keyswithkotlin.presentation.ShowCaseProperty
import com.aman.keyswithkotlin.presentation.ShowCaseView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PasswordScreen(
    _state: StateFlow<PasswordState>,
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
    unHidePasswordChar: () -> Flow<BiometricStatus>,
    bottomBar: @Composable (() -> Unit)
) {

    val targets = remember { mutableStateMapOf<String, ShowCaseProperty>() }

    val state = _state.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
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
    var onLongClicked by remember { mutableStateOf(false) }
    var isAlertDialogVisible by remember { mutableStateOf(false) }

    //for UIEvents
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

    //for Shimmer effect
//    var isLoading by remember {
//        mutableStateOf(true)
//    }
//    LaunchedEffect(Unit) {
//        delay(2000)  // This delay ensures that isVisible is set to true after the initial composition
//        isLoading = false
//    }


    // Define a separate lambda for handling back navigation
    val handleNavigation: (String) -> Unit = { identifier ->
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("RootNode")
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(title = "Home")
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 7.dp),
                                query = searchtext.value,
                                onQueryChange = {
                                    onEvent(PasswordEvent.OnSearchTextChange(it))
                                    searchtext.value = it
                                },
                                onSearch = {},
                                colors = SearchBarDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                placeholder = {
                                    Text(
                                        modifier = Modifier.onGloballyPositioned { coordinates ->
                                            targets["Search"] = ShowCaseProperty(
                                                index = 0,
                                                coordinate = coordinates,
                                                title = "Search passwords",
                                                subTitle = "Click here, to search password"
                                            )
                                        },
                                        text = "Search"
                                    )
                                },
                                active = isSearchBarActive,
                                onActiveChange = { isSearchBarActive = it },
                                leadingIcon = {
                                    IconButton(
                                        onClick = {}
                                    ) {
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
                                        searchedPasswords?.let { searchedPasswords ->
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
                                if (state.value.passwords.isEmpty()) {
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
                                                Text(
                                                    text = "Recently used passwords",
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                        }
                                        items(state.value.recentlyUsedPasswords.take(3)) { password ->
                                            PasswordItem(
                                                password = password,
                                                onItemClick = {
                                                    itemToView.value = password
                                                    viewPassword = true
                                                },
                                                onItemLongClick = {
                                                    itemToView.value = password
                                                    onLongClicked = true
                                                },
                                                onMoreClick = {
                                                    itemToView.value = password
                                                    onLongClicked = true
                                                }
                                            )
//                                            ShimmerListItem(
//                                                isLoading = isLoading,
//                                                contentAfterLoading = {
//
//                                                })
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

                                        items(state.value.passwords) { password ->
                                            PasswordItem(
                                                password = password,
                                                onItemClick = {
                                                    itemToView.value = password
                                                    viewPassword = true
                                                },
                                                onItemLongClick = {
                                                    itemToView.value = password
                                                    onLongClicked = true
                                                },
                                                onMoreClick = {
                                                    itemToView.value = password
                                                    onLongClicked = true
//                                                    onEvent(PasswordEvent.UpdateLastUsedPasswordTimeStamp(password = password))
                                                },
                                                onClickGloballyPositioned = if (password == state.value.passwords.first()){
                                                    {coordinates->
                                                        targets["ViewPssword"] =
                                                            ShowCaseProperty(
                                                                index = 1,
                                                                coordinate = coordinates,
                                                                title = "View password",
                                                                subTitle = "Click here to view password"
                                                            )
                                                    }
                                                } else{null},
                                                onLongPressGloballyPositioned = if (password == state.value.passwords.first()){
                                                    {coordinates->
                                                        targets["PasswordOption"] =
                                                            ShowCaseProperty(
                                                                index = 2,
                                                                coordinate = coordinates,
                                                                title = "View password options",
                                                                subTitle = "Click here or Long press on password item to view password options"
                                                            )
                                                    }
                                                } else{null}
                                            )
//                                            ShimmerListItem(
//                                                isLoading = isLoading,
//                                                contentAfterLoading = {
//
//                                                })
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


                if (state.value.isLoading) {
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
                dragHandle = null,
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                ViewPasswordScreen(
                    itemToView.value!!,
                    onCloseButtonClick = {
                        viewPassword = false
                    },
                    unHidePasswordChar = unHidePasswordChar,
                    onEditButtonClick = {
                        onSharedPasswordEvent(SharedPasswordEvent.onEditItem(itemToView.value!!))
                        navigateToAddEditPasswordScreen()
                    },
                    onShareButtonClick = { password ->
                        onSharedChatEvent(SharedChatEvent.SharePasswordItem(password))
                        navigateToChatUserListScreen()
                    }
                )
            }
        }

        if (onLongClicked) {
            ModalBottomSheet(
                onDismissRequest = {
                    onLongClicked = false
                },
                dragHandle = null,
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                PasswordSettingsScreen(
                    password = itemToView.value!!,
                    onCopyUsernameButtonClick = {
                        copyToClipboardWithTimer(clipboardManager, it.userName)
                        onLongClicked = false
                    },
                    onCopyPasswordButtonClick = {
                        copyToClipboardWithTimer(clipboardManager, it.password)
                        onLongClicked = false
                    },
                    onEditButtonClick = {
                        onSharedPasswordEvent(SharedPasswordEvent.onEditItem(itemToView.value!!))
                        navigateToAddEditPasswordScreen()
                        onLongClicked = false
                    },
                    onDeleteButtonClick = { password ->
                        onEvent(PasswordEvent.DeletePassword(password = password))
                        onLongClicked = false
                    },
                    onShareButtonClick = { password ->
                        onSharedChatEvent(SharedChatEvent.SharePasswordItem(password))
                        navigateToChatUserListScreen()
                        onLongClicked = false
                    }
                )
            }
        }
    }

    ShowCaseView(targets = targets) {

    }
}

private fun copyToClipBoard(
    clipboardManager: ClipboardManager,
    text: String
) {
    clipboardManager.setText(
        AnnotatedString(
            text = text
        )
    )

}

private fun copyToClipboardWithTimer(
    clipboardManager: ClipboardManager,
    text: String
) {
    // Copy the text to the clipboard
    clipboardManager.setText(
        AnnotatedString(
            text = text
        )
    )
    // Start a 10-second countdown timer
    val timer = object : CountDownTimer(10000, 1000) {
        override fun onTick(milliSecondCount: Long) {
            Log.d("TAG", "Counter Running:" + milliSecondCount / 1000);
            // This method is called every second while the timer is running.
            // You can update a UI element to show the remaining time if needed.
        }

        override fun onFinish() {
            // This method is called when the timer finishes (after 10 seconds).
            // Clear the clipboard contents here.
            clipboardManager.setText(AnnotatedString(text = ""))
        }
    }

    // Start the timer
    timer.start()
}

