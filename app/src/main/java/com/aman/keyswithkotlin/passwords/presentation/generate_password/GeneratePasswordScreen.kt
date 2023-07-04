package com.aman.keyswithkotlin.passwords.presentation.generate_password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratePasswordScreen(
    viewModel: GeneratePasswordViewModel = hiltViewModel(),
    navigateToPasswordScreen:()->Unit,
    navigateToAddEditPasswordScreen:()->Unit
) {
    val state = viewModel.state.value

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "Generate Password") }, navigationIcon = {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.clickable { navigateToPasswordScreen()})
            })
        }) { innnerPaddng ->
        Column(modifier = Modifier.padding(innnerPaddng)) {
            Text(text = "Generated password: ${state.generatedPassword}")
            Text(text = state.slider.toString())
            Slider(
                modifier = Modifier.semantics { contentDescription = "Localized Description" },
                value = state.slider.toFloat(),
                onValueChange = {
                    viewModel.onEvent(GeneratePasswordEvent.ChangeSliderValueChange(it.toInt()))
                },
                steps = 8,
                valueRange = 4f..40f,
                onValueChangeFinished = {
                    viewModel.onEvent(GeneratePasswordEvent.GeneratePassword)
                }
            )
        }
    }

}