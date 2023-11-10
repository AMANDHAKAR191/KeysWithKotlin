package com.aman.keyswithkotlin.setting.presentation

import UIEvents
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.DeviceType
import com.aman.keyswithkotlin.core.LockAppType
import com.aman.keyswithkotlin.core.components.ShowInfoToUser
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.google.android.gms.common.api.ApiException
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStreamReader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    _state: StateFlow<SettingState>,
    displayName: String,
    email: String,
    photoUrl: String,
    packageName: String,
    onEvent: (SettingEvent) -> Unit,
    eventFlowState: SharedFlow<UIEvents>,
    autofillManager: AutofillManager,
    bottomBar: @Composable (() -> Unit),
    navigateToProfileScreen: () -> Unit,
    navigateToAppInfoScreen: () -> Unit,
    navigateToManageDevicesScreen: () -> Unit,
    openPrivacyPolicy: () -> Unit,
    openTermsAndCondition: () -> Unit,
    openContactUs: () -> Unit,
) {

    val state = _state.collectAsState()
    val scope = rememberCoroutineScope()

    val customSpacerWidth = 20.dp

    var lockAppSelectedOption by remember {
        mutableStateOf("")
    }
    var isAutofillServiceEnabled by remember { mutableStateOf(false) }
    var isReadCSV by remember { mutableStateOf(false) }

    LaunchedEffect(state.value.lockAppSelectedOption) {
        lockAppSelectedOption = state.value.lockAppSelectedOption
    }
    LaunchedEffect(true) {
        isAutofillServiceEnabled = autofillManager.hasEnabledAutofillServices()
    }

    //todo for UIEvents

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    isAutofillServiceEnabled = autofillManager.hasEnabledAutofillServices()
                } catch (it: ApiException) {

                }
            }
        }
    var showErrorDialog = remember {
        mutableStateOf(false)
    }
    ShowInfoToUser(
        showDialog = showErrorDialog.value,
        title = "Error",
        message = "Hello",
        onRetry = {
            showErrorDialog.value = false
        }
    )


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Setting")
                },
                colors = TopAppBarDefaults.topAppBarColors(),
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            bottomBar()
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Header(
                    photoUrl = photoUrl,
                    displayName = displayName,
                    email = email,
                    navigateToProfileScreen = {
                        navigateToProfileScreen()
                    })
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                                )
                                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                                .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CustomText(text = "Lock App", description = "")
                                MenuSample(
                                    selectedOption = lockAppSelectedOption,
                                    updateLockAppSetting = {
                                        onEvent(SettingEvent.UpdateLockAppSetting(it.toString()))
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            SingleDeviceCard(
                                loggedInDeviceList = state.value.loggedInDeviceList,
                                navigateToManageDevicesScreen = navigateToManageDevicesScreen
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CustomText(text = "Autofill Password", description = "")
                                SwitchWithIcon(
                                    checked = isAutofillServiceEnabled,
                                    onCheckedChange = {
                                        isAutofillServiceEnabled = it
                                        if (it) {
                                            val intent =
                                                Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                                            intent.data = Uri.parse("package:" + packageName)
                                            launcher.launch(intent)
                                        } else {
                                            autofillManager.disableAutofillServices()
                                        }
                                    })
                            }
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            CustomText(text = "Import Passwords", description = "", onClick = {
                                isReadCSV = true
                            })
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            if (isReadCSV) {
                                CsvReaderScreen(
                                    passwordImported = {
                                        onEvent(SettingEvent.StoreImportedPasswords(it))
                                    }
                                )
                            }
                            CustomText(text = "User Guide", description = "", onClick = {
                                onEvent(SettingEvent.EnableTutorial)
                            })
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            CustomText(text = "App Info", description = "", onClick = {
                                navigateToAppInfoScreen()
                            })
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            CustomText(text = "Contact Us", description = "", onClick = {
//                                openContactUs()
                                showErrorDialog.value = true
                            })
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            CustomText(text = "Privacy Policy", description = "", onClick = {
                                openPrivacyPolicy()
                            })
                            Spacer(modifier = Modifier.height(customSpacerWidth))
                            CustomText(text = "Terms & conditions", description = "", onClick = {
                                openTermsAndCondition()
                            })
                        }
                    }
                )
            }
        }
    )
}


@Composable
fun CustomText(
    text: String,
    description: String,
    customSpacerWidth: Dp = 10.dp,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.then(if (onClick == null) Modifier else Modifier.clickable { onClick() }),
    ) {
        Text(
            text = text, fontSize = 20.sp,
            fontWeight = FontWeight.W500,
            color = textColor
        )
        Text(
            text = "description", fontSize = 18.sp,
            fontWeight = FontWeight.W300,
            color = textColor
        )
        Spacer(modifier = Modifier.height(customSpacerWidth))
    }
}


@Composable
fun MenuSample(
    selectedOption: String,
    updateLockAppSetting: (LockAppType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        Text(text = selectedOption, fontSize = 12.sp, modifier = Modifier.clickable {
            expanded = true
        })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                updateLockAppSetting(LockAppType.IMMEDIATELY)
                expanded = false
            }, text = {
                Text("Immediately")
            })
            DropdownMenuItem(onClick = {
                updateLockAppSetting(LockAppType.AFTER_1_MINUTE)
                expanded = false
            }, text = {
                Text("After 1 Minute")
            })
            DropdownMenuItem(onClick = {
                updateLockAppSetting(LockAppType.NEVER)
                expanded = false
            }, text = {
                Text("Never")
            })
        }
    }
}

@Composable
fun Header(
    photoUrl: String,
    displayName: String,
    email: String,
    navigateToProfileScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(80.dp)
            .clickable {
                navigateToProfileScreen()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(55.dp)
                .clip(
                    CircleShape
                )
                .background(Color.Yellow)
                .padding(2.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = displayName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.W700
            )
            Text(
                text = email,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SingleDeviceCard(
    loggedInDeviceList: List<DeviceData>,
    navigateToManageDevicesScreen: () -> Unit
) {
    var isDevicesVisible by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomText(
                text = "Devices (${loggedInDeviceList.size})",
                description = "",
                onClick = { isDevicesVisible = !isDevicesVisible })
            if (isDevicesVisible) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .clickable {
                                navigateToManageDevicesScreen()
                            },
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Manage Devices")
                    }
                }
            }
        }
        AnimatedVisibility(visible = isDevicesVisible) {
            Column {
                val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
                var isCurrentUserPrimary by remember { mutableStateOf(false) }
                //to check is current device is primary
                for (deviceData in loggedInDeviceList) {
                    if (deviceData.deviceId == deviceInfo.getDeviceId()) {
                        if (deviceData.deviceType == DeviceType.Primary.toString()) {
                            isCurrentUserPrimary = true
                            break
                        }
                    }
                }
                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(loggedInDeviceList) { deviceData ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            shape = CardDefaults.elevatedShape,
                            colors = CardDefaults.elevatedCardColors()
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                )
                            ) {
                                Text(text = "Device name: ${deviceData.deviceName}")
                                Text(text = "Device type: ${deviceData.deviceType}")
                                Text(text = "Last login time: ${deviceData.lastLoginTimeStamp}")
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchWithIcon(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        }
    )
}


@Composable
fun CsvReaderScreen(
    passwordImported: (List<Password>) -> Unit
) {
    var passwordList = remember { mutableStateListOf<Password>() }
    var csvData by remember { mutableStateOf<List<List<String>>?>(null) }
    var contentResolver = LocalContext.current.contentResolver
    val openCsvFile =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    val timeStampUtil = TimeStampUtil()
                    val tempPasswordList = mutableListOf<Password>()
                    val inputStream = contentResolver.openInputStream(uri)
                    val csvReader = CSVReader(InputStreamReader(inputStream))
                    var recordList: Array<String>?
                    while (csvReader.readNext().also { recordList = it } != null) {
                        recordList?.let { record ->
                            val name = record[0]
                            val link = record[1]
                            val username = record[2]
                            val password = record[3]

                            // "note" is present in the CSV but not required
                            val note = if (record.size > 4) record[4] else ""

                            val passwordModel = Password(
                                userName = username,
                                password = password,
                                websiteName = name,
                                listOf(link),
                                timestamp = timeStampUtil.generateTimestamp()
                            )
                            tempPasswordList.add(passwordModel)
                        }
                    }

                    println("tempPasswordList: $tempPasswordList")
                    tempPasswordList.removeFirst()
                    passwordList.swapList(tempPasswordList)
                    println("passwordList: $passwordList")

                    passwordImported(passwordList)

                    csvReader.close()
                } catch (e: Exception) {
                    // Handle exceptions (e.g., file not found, parsing errors)
                }

            }
        }

    println("passwordList1: $passwordList")


    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(passwordList) {
                PasswordItem(password = it)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Button(onClick = { openCsvFile.launch(arrayOf("*/*")) }) {
            Text("Select CSV File")
        }
    }
}

@Composable
fun PasswordItem(password: Password) {
    // Customize the appearance of a single password item here
    Text(text = "Website: ${password.websiteName}")
    Text(text = "Username: ${password.password}")
    Divider() // Optional divider between items
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}
