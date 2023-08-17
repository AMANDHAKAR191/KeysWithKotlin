package com.aman.keyswithkotlin.chats.presentation

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aman.keyswithkotlin.core.MyPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedChatViewModel @Inject constructor(
    private val myPreference: MyPreference
) : ViewModel() {
    private val _state = mutableStateOf(SharedChatState())
    val state: State<SharedChatState> = _state

    fun onEvent(event: SharedChatEvent) {
        when (event) {
            is SharedChatEvent.OpenSharedChat -> {
                _state.value = state.value.copy(
                    person = event.person,
//                    commonChatRoomId = event.chatRoomId
                )
                myPreference.commonChatRoomId = state.value.person?.commonChatRoomId
                println("sharedChatViewModel event.person: ${state.value.person}")
//                println("sharedChatViewModel event.chatRoomId: ${state.value.commonChatRoomId}")
            }

            else -> {}
        }
    }
}