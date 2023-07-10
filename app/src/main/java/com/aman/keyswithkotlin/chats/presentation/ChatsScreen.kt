package com.aman.keyswithkotlin.chats.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.aman.keyswithkotlin.passwords.presentation.componants.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    title:String,
    bottomBar: @Composable (() -> Unit)
) {

    Scaffold(
        topBar = {
            TopBar(
                title = "Chats",
                onClickActionButton = {}
            )
        },
        bottomBar = {
            bottomBar()
        }, content = { innerPadding ->

        }
    )
}