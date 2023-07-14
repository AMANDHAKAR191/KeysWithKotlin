package com.aman.keyswithkotlin.chats.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedChatViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.OpenChat -> {
                state.value.person = event.person
            }

            is ChatEvent.resetSharedViewModel -> {
                _state.value = state.value.copy(
                    person = null
                )
            }
        }
    }
}