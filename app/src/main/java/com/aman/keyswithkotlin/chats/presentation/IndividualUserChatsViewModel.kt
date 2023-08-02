package com.aman.keyswithkotlin.chats.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.chats.presentation.ChatEvent
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IndividualUserChatsViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    private val _state = mutableStateOf<ChatMessagesState>(ChatMessagesState())
    val state: State<ChatMessagesState> = _state

    private val _eventFlow = MutableSharedFlow<AddEditPasswordViewModel.UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        getChatUserList()
    }

    fun onEvent(event: ChatEvent){
        when(event){
            is ChatEvent.SendMessage -> {
                println("Message: ${state.value.chatMessage}")
                viewModelScope.launch {
                    chatUseCases.sendMessage(
                        ChatModelClass(
                            message = state.value.chatMessage,
                            publicUid = "amandhaker191",
                            type = "text"
                        )
                    ).collect{ response ->
                        when (response) {
                            is Response.Loading -> {

                            }

                            is Response.Success<*, *> -> {
                                _state.value = state.value.copy(
                                    chatMessage = ""
                                )
                                _eventFlow.emit(
                                    AddEditPasswordViewModel.UiEvent.ShowSnackBar(
                                        message = response.data.toString()
                                    )
                                )
                            }

                            is Response.Failure -> {

                            }
                        }
                        _eventFlow.emit(
                            AddEditPasswordViewModel.UiEvent.savePassword
                        )
                    }
                }
            }
            is ChatEvent.OnMessageEntered->{
                println("${event.value}")
                _state.value = state.value.copy(
                    chatMessage = event.value
                )
            }
            else -> {}
        }
    }

    private fun getChatUserList() {
        viewModelScope.launch(Dispatchers.IO) {
            chatUseCases.getUserChatMessages("kirandhaker123tushar08152002").collect { response ->
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