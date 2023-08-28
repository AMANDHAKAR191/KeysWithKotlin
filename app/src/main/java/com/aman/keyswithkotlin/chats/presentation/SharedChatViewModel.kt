package com.aman.keyswithkotlin.chats.presentation

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aman.keyswithkotlin.chats.presentation.individual_chat.ChatMessageState
import com.aman.keyswithkotlin.core.MyPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedChatViewModel @Inject constructor(
    private val myPreference: MyPreference
) : ViewModel() {

    private var _state = MutableStateFlow<SharedChatState>(SharedChatState())
    var state: StateFlow<SharedChatState> = _state.asStateFlow()

    fun onEvent(event: SharedChatEvent) {
        when (event) {
            is SharedChatEvent.OpenSharedChat -> {
                _state.value = state.value.copy(
                    person = event.person,
                )
                myPreference.commonChatRoomId = state.value.person?.commonChatRoomId
                println("sharedChatViewModel event.person: ${state.value.person}")
            }

            is SharedChatEvent.ShareNoteItem -> {
                _state.value = state.value.copy(
                    sharedNoteItem = event.noteItem
                )
            }
            is SharedChatEvent.SharePasswordItem -> {
                _state.value = state.value.copy(
                    sharedPasswordItem = event.passwordItem
                )
            }
        }
    }
}