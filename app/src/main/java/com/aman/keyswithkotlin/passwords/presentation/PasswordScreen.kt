package com.aman.keyswithkotlin.passwords.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.passwords.presentation.componants.PasswordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    passwordViewModel: PasswordViewModel = hiltViewModel()
) {
    val state = passwordViewModel.state.value

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize() ,contentAlignment = Center) {
                CircularProgressIndicator()
            }
        }
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.passwords){password->
                    PasswordItem(password = password, onDeleteClick = {})
                }
            }
        }
    }
}