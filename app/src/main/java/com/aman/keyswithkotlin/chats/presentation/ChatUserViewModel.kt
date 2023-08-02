package com.aman.keyswithkotlin.chats.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatUserViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
):ViewModel() {

    private val _eventFlow = MutableSharedFlow<AddEditPasswordViewModel.UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    init {
        getChatUsers()
    }

    private fun getChatUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            chatUseCases.getChatUsers().collect{response->
                println(this.coroutineContext)
                withContext(Dispatchers.Main){
                    println(this.coroutineContext)
                    when (response) {
                        is Response.Success<*, *> -> {
                            _state.value = state.value.copy(
                                chatUsersList = response.data as List<UserPersonalChatList>
                            )
                        }

                        is Response.Failure -> {
                            _state.value = state.value.copy(
                                error  = response.e.message ?: "Unexpected error occurred",
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