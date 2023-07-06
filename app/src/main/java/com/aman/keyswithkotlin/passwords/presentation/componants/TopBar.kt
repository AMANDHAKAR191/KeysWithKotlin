package com.aman.keyswithkotlin.passwords.presentation.componants

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onClickActionButton:()->Unit
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        modifier = Modifier.fillMaxWidth(),
        actions = {
            IconButton(onClick = {
                onClickActionButton()
            }) {
                Icon(Icons.Default.Person2, contentDescription = "Open Profile")
            }
        }
    )
}