package com.aman.keys.chats.presentation.chat_user_list

import androidx.lifecycle.ViewModel
import com.aman.keys.core.MyPreference
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