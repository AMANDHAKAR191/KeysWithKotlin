package com.aman.keyswithkotlin.setting.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.keyswithkotlin.chats.presentation.noRippleEffect
import com.aman.keyswithkotlin.core.Constants.EXIT_DURATION
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    bottomBar: @Composable (() -> Unit),
    navigateToProfileScreen: () -> Unit
) {

    var isDevicesVisible by remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(EXIT_DURATION.toLong())  // This delay ensures that isVisible is set to true after the initial composition
        isVisible = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(title = "Keys",
                onClickActionButton = {
                    navigateToProfileScreen()
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                            )
                            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                            .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Lock App", fontSize = 20.sp)
                            Text(text = "After1 Minute", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Column {
                            Text(
                                text = "Devices",
                                fontSize = 20.sp,
                                modifier = Modifier.noRippleEffect {
                                    isDevicesVisible = !isDevicesVisible
                                }
                            )
                            if (isDevicesVisible) {
                                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                    item {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(text = "Devices name1", fontSize = 20.sp)
                                            Text(text = "Devices id1", fontSize = 20.sp)
                                        }
                                    }
                                    item {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(text = "Devices name2", fontSize = 20.sp)
                                            Text(text = "Devices id2", fontSize = 20.sp)
                                        }
                                    }
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "User Guide", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "App Info", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Contact Us", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Privacy Policy", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Terms & conditions", fontSize = 20.sp)
                    }
                }
            )
        }
    )
}

@Preview
@Composable
fun Preview() {
    SettingScreen(bottomBar = { /*TODO*/ }) {

    }
}