package com.aman.keys.auth.presentation.profile

import UIEvents
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aman.keys.auth.domain.repository.RevokeAccessResponse
import com.aman.keys.auth.domain.repository.SignOutResponse
import com.aman.keys.auth.presentation.AuthEvent
import com.aman.keys.auth.presentation.profile.components.RevokeAccess
import com.aman.keys.auth.presentation.profile.components.SignOut
import com.aman.keys.core.Constants
import com.aman.keys.core.Constants.REVOKE_ACCESS_MESSAGE
import com.aman.keys.core.Constants.SIGN_OUT
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    _state: StateFlow<ProfileState>,
    displayName: String,
    photoUrl: String,
    onEvent: (AuthEvent) -> Unit,
    eventFlowState: SharedFlow<UIEvents>,
    revokeAccessResponse: RevokeAccessResponse,
    signOutResponse: SignOutResponse,
    onSignOut: () -> Unit,
    onRevokeAccess: () -> Unit,
    navigateToAuthScreen: () -> Unit,
    navigateBack: () -> Unit
) {

    val state = _state.collectAsState()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val coroutineScope = rememberCoroutineScope()
    var isAuthorizationAlertDialogVisible by remember { mutableStateOf(false) }
    var codes = remember {
        mutableStateListOf<Int>()
    }
    var receivedCode = remember {
        mutableIntStateOf(0)
    }
    var isAuthorizationCodeMatched = remember {
        mutableStateOf(false)
    }
    var isCodeSelected = remember {
        mutableStateOf(false)
    }
    var isCodeInvaildError = remember {
        mutableStateOf(false)
    }
    var showError = remember {
        mutableStateOf(false)
    }

    var openMenu by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isCodeInvaildError.value){
        showError != showError
    }
    LaunchedEffect(key1 = true) {
        eventFlowState.collectLatest { event ->
            when (event) {

                is UIEvents.NavigateToNextScreen -> {
                    isAuthorizationAlertDialogVisible = false
                }

                is UIEvents.ShowAuthorizationAlertDialog -> {
                    println("codes1: $codes")
                    //initialize here
                    receivedCode.value = event.authorizationCode
                    codes.addAll(createShuffledList(event.authorizationCode))
                    println("codes2: $codes")
                    isAuthorizationAlertDialogVisible = true
                    println("codes3: $codes")
                }

                is UIEvents.HideAuthorizationAlertDialog -> {
                    isAuthorizationAlertDialogVisible = false
                }

                else -> {}
            }
        }

    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "App Info", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = { navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            openMenu = !openMenu
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = openMenu,
                        onDismissRequest = {
                            openMenu = !openMenu
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(text = SIGN_OUT)
                            },
                            onClick = {
                                onSignOut()
                                openMenu = !openMenu
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = Constants.REVOKE_ACCESS)
                            },
                            onClick = {
                                onRevokeAccess()
                                openMenu = !openMenu
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors()
            )
//            ProfileTopBar(
//                signOut = {
//                    onSignOut()
//                },
//                revokeAccess = {
//                    onRevokeAccess()
//                },
//                navigateToPasswordScreen = {
//                    navigateBack()
////                    handleBackNavigation("PasswordScreen")
//                }
//            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(48.dp)
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(96.dp)
                        .height(96.dp)
                )
                Text(
                    text = displayName,
                    fontSize = 24.sp
                )
//                Column {
//                    val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
//                    var isCurrentUserPrimary by remember { mutableStateOf(false) }
//                    //to check is current device is primary
//                    for (deviceData in state.value.loggedInDeviceList) {
//                        if (deviceData.deviceId == deviceInfo.getDeviceId()) {
//                            if (deviceData.deviceType == DeviceType.Primary.toString()) {
//                                isCurrentUserPrimary = true
//                                break
//                            }
//                        }
//                    }
//                    LazyColumn {
//                        items(state.value.loggedInDeviceList) { deviceData ->
//                            Column {
//                                Text(text = "Device ID: ${deviceData.deviceId}")
//                                Text(text = "Device Name: ${deviceData.deviceName}")
//                                Text(text = "Device Type: ${deviceData.deviceType}")
//                                Text(text = "Device Authorization: ${deviceData.authorization}")
//                                Text(text = "Last login time: ${deviceData.lastLoginTimeStamp}")
//                                //if current device is primary device then show remove access for secondary devices
//                                if (isCurrentUserPrimary) {
//                                    if (deviceData.deviceType == DeviceType.Secondary.toString()) {
//                                        Button(onClick = {
//                                            if (deviceData.authorization == Authorization.Authorized.toString()) {
//                                                println("deviceName: ${deviceData.deviceName} authorization: ${deviceData.authorization}")
//                                                onEvent(
//                                                    AuthEvent.RemoveAuthorizationAccess(
//                                                        deviceData.deviceId!!
//                                                    )
//                                                )
//                                            } else {
//                                                println("deviceName: ${deviceData.deviceName} authorization: ${deviceData.authorization}")
//                                                onEvent(AuthEvent.GiveAuthorizationAccess(deviceData.deviceId!!))
//                                            }
//                                        }) {
//                                            if (deviceData.authorization == Authorization.Authorized.toString()) {
//                                                Text(text = "Remove Access")
//                                            } else {
//                                                Text(text = "Give Access")
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            }

            if (isAuthorizationAlertDialogVisible) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onDismissRequest.
                    },
                    title = {
                        Column {
                            Text(
                                text = "Grant Permission!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            InputChipsRow(values = codes, onChipClick = {
                                isCodeSelected.value = it != 0
                                isAuthorizationCodeMatched.value = it == receivedCode.value
                            })

                            if (showError.value){
                                Text(text = "Invalid code", color = MaterialTheme.colorScheme.error)
                                println("code is invalid")
                            }
                        }
//                        Row {
//                            codes.forEach {code->
//                                Box(
//                                    modifier = Modifier
//                                        .size(45.dp)
//                                        .clip(CircleShape)
//                                        .background(Color.Green),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(text = "$code")
//                                }
//                                Spacer(modifier = Modifier.width(10.dp))
//                            }
//                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (isAuthorizationCodeMatched.value){
                                onEvent(AuthEvent.GrantAccessPermission)
                            }else{
                                isCodeInvaildError.value = true
                            }
                        }, enabled = isCodeSelected.value) {
                            Text("Approve")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                onEvent(AuthEvent.DeclineAuthorizationAccessProcess)
                            }
                        ) {
                            Text("Decline")
                        }
                    }
                )
            }
        }
    )

    SignOut(
        signOutResponse = signOutResponse,
        navigateToAuthScreen = { signedOut ->
            if (signedOut) {
                navigateToAuthScreen()
            }
        }
    )

    fun showSnackBar() = coroutineScope.launch {
        val result = snackBarHostState.showSnackbar(
            message = REVOKE_ACCESS_MESSAGE,
            actionLabel = SIGN_OUT
        )
        if (result == SnackbarResult.ActionPerformed) {
            onSignOut()
        }
    }

    RevokeAccess(
        revokeAccessResponse = revokeAccessResponse,
        navigateToAuthScreen = { accessRevoked ->
            if (accessRevoked) {
                navigateToAuthScreen()
            }
        },
        showSnackBar = {
            showSnackBar()
        }
    )
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun InputChipsRow(
//    values: List<Int>,
//    onChipClick: (Int) -> Unit
//) {
//    var chip1ValueSelected by remember {
//        mutableStateOf(true)
//    }
//    var chip2ValueSelected by remember {
//        mutableStateOf(false)
//    }
//    var chip3ValueSelected by remember {
//        mutableStateOf(false)
//    }
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        InputChip(selected = chip1ValueSelected, onClick = {
////            viewModel.onEvent(AddEditExpenseEvent.ChangeExpenseType("Income"))
//            chip1ValueSelected = !chip1ValueSelected
//            if (chip2ValueSelected) {
//                chip2ValueSelected = !chip2ValueSelected
//            }else if (chip3ValueSelected){
//                chip3ValueSelected = !chip3ValueSelected
//            }
//        }, label = { Text(text = values.get(0).toString()) },
//            modifier = Modifier
//                .padding(all = 10.dp)
//        )
//        InputChip(selected = chip2ValueSelected, onClick = {
////            viewModel.onEvent(AddEditExpenseEvent.ChangeExpenseType("Income"))
//            chip2ValueSelected = !chip2ValueSelected
//            if (chip1ValueSelected) {
//                chip1ValueSelected = !chip1ValueSelected
//            }else if (chip3ValueSelected){
//                chip3ValueSelected = !chip3ValueSelected
//            }
//        }, label = { Text(text = values.get(0).toString()) },
//            modifier = Modifier
//                .padding(all = 10.dp)
//        )
//        InputChip(selected = chip3ValueSelected, onClick = {
////            viewModel.onEvent(AddEditExpenseEvent.ChangeExpenseType("Income"))
//            chip3ValueSelected = !chip3ValueSelected
//            if (chip1ValueSelected) {
//                chip1ValueSelected = !chip1ValueSelected
//            }else if (chip2ValueSelected){
//                chip2ValueSelected = !chip2ValueSelected
//            }
//        }, label = { Text(text = values.get(0).toString()) },
//            modifier = Modifier
//                .padding(all = 10.dp)
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputChipsRow(
    values: List<Int>,
    onChipClick: (Int) -> Unit
) {
    val selectedChips = remember { mutableStateListOf<Int>() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        values.forEach { value ->
            val isSelected = value in selectedChips

            InputChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        selectedChips.remove(value)
                        onChipClick(0)
                    } else {
                        selectedChips.clear()
                        selectedChips.add(value)
                        onChipClick(value)
                    }
                },
                label = { Text(text = value.toString()) },
                modifier = Modifier.padding(all = 10.dp)
            )
        }
    }
}


fun createShuffledList(parameter: Int): List<Int> {
    val random1 = (10..99).random()
    val random2 = (10..99).random()

    val numbers = listOf(random1, random2, parameter)

    // Now, you can use the 'shuffledList' in your Composable view.
    // For example, you can create a LazyColumn to display the numbers.
    return numbers.shuffled()
}