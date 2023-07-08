package com.aman.keyswithkotlin.notes.presentation.note_screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    title:String,
    bottomBar: @Composable (() -> Unit)
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) }
            )
        },
        bottomBar = {
            bottomBar()
        }, content = { innerPadding ->

        }
    )
}