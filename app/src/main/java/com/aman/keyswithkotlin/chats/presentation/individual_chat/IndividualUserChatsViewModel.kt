package com.aman.keyswithkotlin.chats.presentation.individual_chat

import UIEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.chats.domain.model.ChatModelClass
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.PublicUID
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class IndividualUserChatsViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val chatUseCases: ChatUseCases,
    private val myPreference: MyPreference,
    @PublicUID
    private val publicUID: String,
) : ViewModel() {

    val displayName get() = authUseCases.displayName
    val photoUrl get() = authUseCases.photoUrl

    private var _state = MutableStateFlow<ChatMessageState>(ChatMessageState())
    var state: StateFlow<ChatMessageState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        FirebaseMessaging.getInstance().subscribeToTopic(publicUID)
        _state.value = state.value.copy(
            senderPublicUID = publicUID
        )
        myPreference.commonChatRoomId?.let {
            getChatUserList(it)
        }
    }

    fun onEvent(event: ChatMessageEvent) {
        when (event) {
            is ChatMessageEvent.SendMessage -> {
                viewModelScope.launch {
                    chatUseCases.sendMessage(
                        event.commonChatRoomId,
                        ChatModelClass(
                            message = state.value.chatMessage,
                            publicUid = publicUID,
                            type = "text",
                            passwordModelClass = event.passwordItemValue
                        )
                    ).collectLatest { response ->
                        when (response) {
                            is Response.Loading -> {

                            }

                            is Response.Success<*, *> -> {
                                if (event.passwordItemValue != null) {
                                    _eventFlow.emit(
                                        UIEvents.SendNotification(
                                            publicUID,
                                            messageBody = "shared a Password"
                                        )
                                    )
                                }
                                _eventFlow.emit(
                                    UIEvents.SendNotification(
                                        publicUID,
                                        messageBody = state.value.chatMessage
                                    )
                                )
                                _state.update {
                                    it.copy(
                                        chatMessage = ""
                                    )
                                }
                                event.person?.let {
                                    val userPersonalChatList = UserPersonalChatList(
                                        otherUserPublicUid = publicUID,
                                        otherUserPublicUname = displayName.invoke(),
                                        otherUserProfileUrl = photoUrl.invoke(),
                                        commonChatRoomId = it.commonChatRoomId,
                                        commonEncryptionKey = it.commonEncryptionKey,
                                        commonEncryptionIv = it.commonEncryptionIv
                                    )
                                    chatUseCases.createUserInReceiverChat(it.otherUserPublicUid!!, userPersonalChatList).collectLatest{
                                    }
                                }
                            }

                            is Response.Failure -> {

                            }
                        }
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
            chatUseCases.getUserChatMessages(commonChatRoomId).collect { response ->
                println(this.coroutineContext)
                withContext(Dispatchers.Main) {
                    println(this.coroutineContext)
                    when (response) {
                        is Response.Success<*, *> -> {
                            _state.update { it.copy(chatMessagesList = response.data as List<ChatModelClass>, isMessageReceived = (0..9).random()) }
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