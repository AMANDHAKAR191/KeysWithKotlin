package com.aman.keyswithkotlin.passwords.presentation.generate_password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.core.Constants
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.ShareGeneratedPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.SharedPasswordEvent
import com.aman.keyswithkotlin.passwords.presentation.componants.CustomSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratePasswordScreen(
    viewModel: GeneratePasswordViewModel = hiltViewModel(),
    navigateToPasswordScreen:()->Unit,
    navigateToAddEditPasswordScreen:(String)->Unit
) {
    val state = viewModel.state.value
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Generate Password") },
                navigationIcon = {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { navigateToPasswordScreen() })
                })
        }) { innnerPaddng ->
        Column(
            modifier = Modifier
                .padding(innnerPaddng)
                .padding(all = 10.dp)
                .fillMaxSize()
        ) {
            Text(text = "Generated password: ${state.generatedPassword}")
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Password length = ${state.slider.toString()}")
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
            CustomSwitch(
                label = Constants.IDENTIFIER_UPPER_CASE,
                checked = state.upperCaseAlphabet,
                onCheckedChange = {
                    viewModel.onEvent(
                        GeneratePasswordEvent.ChangeSwitchValueChange(
                            !state.upperCaseAlphabet,
                            Constants.IDENTIFIER_UPPER_CASE
                        )
                    )
                }
            )
            CustomSwitch(
                label = Constants.IDENTIFIER_LOWER_CASE,
                checked = state.lowerCaseAlphabet,
                onCheckedChange = {
                    viewModel.onEvent(
                        GeneratePasswordEvent.ChangeSwitchValueChange(
                            !state.lowerCaseAlphabet,
                            Constants.IDENTIFIER_LOWER_CASE
                        )
                    )
                }
            )
            CustomSwitch(
                label = Constants.IDENTIFIER_NUMBER,
                checked = state.number,
                onCheckedChange = {
                    viewModel.onEvent(
                        GeneratePasswordEvent.ChangeSwitchValueChange(
                            !state.number,
                            Constants.IDENTIFIER_NUMBER
                        )
                    )
                }
            )
            CustomSwitch(
                label = Constants.IDENTIFIER_SPECIAL_CHAR,
                checked = state.specialCharacter,
                onCheckedChange = {
                    viewModel.onEvent(
                        GeneratePasswordEvent.ChangeSwitchValueChange(
                            !state.specialCharacter,
                            Constants.IDENTIFIER_SPECIAL_CHAR
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = {
                    viewModel.onEvent(GeneratePasswordEvent.CopyPassword(clipboardManager))
                }) {
                    Text(text = "Copy")
                }
                Button(onClick = {
                    println("generatedPassword1: ${state.generatedPassword}")
                    navigateToAddEditPasswordScreen(state.generatedPassword)
                }) {
                    Text(text = "Use")
                }
            }
        }
    }

}