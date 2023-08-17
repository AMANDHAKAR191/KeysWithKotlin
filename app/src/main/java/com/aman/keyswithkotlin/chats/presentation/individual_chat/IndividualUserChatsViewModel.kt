package com.aman.keyswithkotlin.chats.presentation.individual_chat

import UIEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.PublicUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IndividualUserChatsViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val myPreference: MyPreference,
    @PublicUID
    private val publicUID: String,
) : ViewModel() {

    private var _state = MutableStateFlow<ChatMessageState>(ChatMessageState())
    var state: StateFlow<ChatMessageState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        _state.value = state.value.copy(
            senderPublicUID = publicUID
        )
//        println("sharedChatViewModel.state.value.commonChatRoomId: ${sharedChatViewModel.state.value.commonChatRoomId}")
//        state.value.commonChatRoomId = sharedChatViewModel.state.value.commonChatRoomId
        println("commonChatRoomId: ${myPreference.commonChatRoomId}")
        myPreference.commonChatRoomId?.let {
            getChatUserList(it)
        }
    }

    fun onEvent(event: ChatMessageEvent) {
        when (event) {
            is ChatMessageEvent.SendMessage -> {
                println("Message: ${state.value.chatMessage}")
                println("commonChatRoomId: ${event.commonChatRoomId}")

                viewModelScope.launch {
                    chatUseCases.sendMessage(
                        event.commonChatRoomId,
                        ChatModelClass(
                            message = state.value.chatMessage,
                            publicUid = publicUID,
                            type = "text"
                        )
                    ).collect { response ->
                        when (response) {
                            is Response.Loading -> {

                            }

                            is Response.Success<*, *> -> {
                                _state.value = state.value.copy(
                                    chatMessage = ""
                                )
                                _eventFlow.emit(
                                    UIEvents.ShowSnackBar(
                                        message = response.data.toString()
                                    )
                                )
                            }

                            is Response.Failure -> {

                            }
                        }
                        _eventFlow.emit(
                            UIEvents.SavePassword
                        )
                    }
                }
            }

            is ChatMessageEvent.OnMessageEntered -> {
                _state.value = state.value.copy(
                    chatMessage = event.value
                )
            }
        }
    }

    private fun getChatUserList(commonChatRoomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            println("commonChatRoomId??: $commonChatRoomId")
            chatUseCases.getUserChatMessages(commonChatRoomId).collect { response ->
                println(this.coroutineContext)
                withContext(Dispatchers.Main) {
                    println(this.coroutineContext)
                    when (response) {
                        is Response.Success<*, *> -> {
                            println("userList: ${response.data as List<*>}")
                            _state.value = state.value.copy(
                                chatMessagesList = response.data as List<ChatModelClass>
                            )
                        }

                        is Response.Failure -> {
                            _state.value = state.value.copy(
                                error = response.e.message ?: "Unexpected error occurred",
                                isLoading = false
                            )
                        }

                        is Response.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }
}