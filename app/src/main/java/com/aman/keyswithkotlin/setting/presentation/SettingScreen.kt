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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar

@Composable
fun SettingScreen(
    bottomBar: @Composable (() -> Unit),
    navigateToProfileScreen: () -> Unit
) {

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
                        Text(text = "Devices", fontSize = 20.sp)
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