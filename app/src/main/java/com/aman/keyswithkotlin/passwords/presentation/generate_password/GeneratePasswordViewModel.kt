package com.aman.keyswithkotlin.passwords.presentation.generate_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.core.Constants
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GeneratePasswordViewModel @Inject constructor(
    private val passwordUseCases: PasswordUseCases
) : ViewModel() {

    private val _state = mutableStateOf(GeneratePasswordState())
    val state: State<GeneratePasswordState> = _state

    init {
        _state.value = state.value.copy(
            generatedPassword = "Generate Password",
            slider = 8,
            upperCaseAlphabet = !state.value.upperCaseAlphabet,
            lowerCaseAlphabet = !state.value.lowerCaseAlphabet,
            number = !state.value.number,
            specialCharacter = !state.value.specialCharacter
        )
        println("inside: GeneratePasswordViewModel")
        generatePassword()
    }

    fun onEvent(event: GeneratePasswordEvent) {
        when (event) {
            is GeneratePasswordEvent.ChangeSliderValueChange -> {
                _state.value = state.value.copy(
                    slider = event.value
                )
            }

            is GeneratePasswordEvent.ChangeSwitchValueChange -> {
                if (event.identifier == Constants.IDENTIFIER_UPPER_CASE) {
                    _state.value = state.value.copy(
                        upperCaseAlphabet = event.value
                    )
                } else if (event.identifier == Constants.IDENTIFIER_LOWER_CASE) {
                    _state.value = state.value.copy(
                        lowerCaseAlphabet = event.value
                    )
                } else if (event.identifier == Constants.IDENTIFIER_NUMBER) {
                    _state.value = state.value.copy(
                        number = event.value
                    )
                } else if (event.identifier == Constants.IDENTIFIER_SPECIAL_CHAR) {
                    _state.value = state.value.copy(
                        specialCharacter = event.value
                    )
                }

                //check if all switch is off then default on anyone by default
                if (
                    !state.value.upperCaseAlphabet &&
                    !state.value.lowerCaseAlphabet &&
                    !state.value.number &&
                    !state.value.specialCharacter
                ) {
                    _state.value = state.value.copy(
                        generatedPassword = "Generate Password",
                        upperCaseAlphabet = true
                    )
                }

            }

            is GeneratePasswordEvent.GeneratePassword -> {
                generatePassword()
            }
            is GeneratePasswordEvent.CopyPassword->{
                copyPasswordToClipBoard(event.clipboardManager)
            }
        }
    }

    private fun generatePassword() {
        viewModelScope.launch {
            passwordUseCases.generatePassword(
                max_length = state.value.slider,
                upperCase = state.value.upperCaseAlphabet,
                lowerCase = state.value.lowerCaseAlphabet,
                numbers = state.value.number,
                specialCharacters = state.value.specialCharacter,
            ).collect {
                _state.value = state.value.copy(
                    generatedPassword = it
                )
            }
        }
    }

    private fun copyPasswordToClipBoard(
        clipboardManager: ClipboardManager
    ) {
        clipboardManager.setText(
            AnnotatedString(
                state.value.generatedPassword
            )
        )
    }
}