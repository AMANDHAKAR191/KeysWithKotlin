package com.aman.keyswithkotlin.chats.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedChatViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(SharedChatState())
    val state: State<SharedChatState> = _state

    fun onEvent(event: SharedChatEvent) {
        when (event) {
            is SharedChatEvent.OpenSharedChat -> {
                state.value.person = event.person
            }

            else -> {}
        }
    }
}