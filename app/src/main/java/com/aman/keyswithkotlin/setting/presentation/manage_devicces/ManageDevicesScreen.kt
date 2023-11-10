package com.aman.keyswithkotlin.setting.presentation.manage_devicces

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.DeviceType
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDevicesScreen(
    _state: StateFlow<ManageDevicesState>,
    onEvent: (ManageDevicesEvent) -> Unit,
    navigateBack: () -> Unit
) {

    val state = _state.collectAsState()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Manage devices", color = MaterialTheme.colorScheme.onSurface) },
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
                colors = TopAppBarDefaults.largeTopAppBarColors()
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
                    var isCurrentUserPrimary by remember { mutableStateOf(false) }
                    //to check is current device is primary
                    for (deviceData in state.value.loggedInDeviceList) {
                        if (deviceData.deviceId == deviceInfo.getDeviceId()) {
                            if (deviceData.deviceType == DeviceType.Primary.toString()) {
                                isCurrentUserPrimary = true
                                break
                            }
                        }
                    }
                    LazyColumn {
                        items(state.value.loggedInDeviceList) { deviceData ->
                            Column {
                                Text(text = "Device ID: ${deviceData.deviceId}")
                                Text(text = "Device Name: ${deviceData.deviceName}")
                                Text(text = "Device Type: ${deviceData.deviceType}")
                                Text(text = "Device Authorization: ${deviceData.authorization}")
                                Text(text = "Last login time: ${deviceData.lastLoginTimeStamp}")
                                //if current device is primary device then show remove access for secondary devices
                                if (isCurrentUserPrimary) {
                                    if (deviceData.deviceType == DeviceType.Secondary.toString()) {
                                        Button(onClick = {
                                            if (deviceData.authorization == Authorization.Authorized.toString()) {
                                                println("deviceName: ${deviceData.deviceName} authorization: ${deviceData.authorization}")
                                                onEvent(ManageDevicesEvent.RemoveAuthorizationAccess(deviceData.deviceId!!))
                                            } else {
                                                println("deviceName: ${deviceData.deviceName} authorization: ${deviceData.authorization}")
                                                onEvent(ManageDevicesEvent.GiveAuthorizationAccess(deviceData.deviceId!!))
                                            }
                                        }) {
                                            if (deviceData.authorization == Authorization.Authorized.toString()) {
                                                Text(text = "Remove Access")
                                            } else {
                                                Text(text = "Give Access")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}