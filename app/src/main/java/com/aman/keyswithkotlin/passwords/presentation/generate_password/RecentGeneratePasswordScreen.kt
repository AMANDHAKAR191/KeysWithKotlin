package com.aman.keyswithkotlin.passwords.presentation.generate_password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.aman.keyswithkotlin.passwords.domain.model.GeneratedPasswordModelClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentGeneratePasswordScreen(
    recentGeneratedPasswordList: MutableList<GeneratedPasswordModelClass> = mutableListOf<GeneratedPasswordModelClass>(),
    navigateToGeneratePasswordScreen: () -> Unit,
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Recent Generate Password") },
                navigationIcon = {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { navigateToGeneratePasswordScreen() })
                })
        }) { innnerPaddng ->
        Column(
            modifier = Modifier
                .padding(innnerPaddng)
                .padding(all = 10.dp)
                .fillMaxSize()
        ) {
            LazyColumn(content = {
                items(recentGeneratedPasswordList.take(10)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = it.passwordCount.toString())
                        Text(text = it.generatedPasswordValue)
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "")
                        }
                    }
                }
            })
        }
    }

}