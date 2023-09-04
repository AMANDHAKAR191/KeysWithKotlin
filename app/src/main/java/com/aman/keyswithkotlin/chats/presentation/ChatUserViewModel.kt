package com.aman.keyswithkotlin.chats.presentation

import UIEvents
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.PublicUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatUserViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    @PublicUID
    private val publicUID: String,
    private val userName:String
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    val _state = mutableStateOf(ChatUserState())
    val state: State<ChatUserState> = _state

    init {
        _state.value = state.value.copy(
            username = userName
        )
        getChatUsers()
    }

    fun onEvent(event: ChatUserEvent) {
        when (event) {
            is ChatUserEvent.CreateChatUser -> {
                viewModelScope.launch {
                    val otherUserPublicUID = validatePublicUID(event.value)
                    if (otherUserPublicUID.equals(publicUID)) {
                        _eventFlow.emit(UIEvents.ShowError("Hey, That's your id. Self chat feature is coming soon"))
                    } else {
                        chatUseCases.getChatProfileDataByPublicUID(otherUserPublicUID)
                            .collect { response ->
                                when (response) {
                                    is Response.Failure -> {
                                        response.e.message?.let {
                                            _eventFlow.emit(UIEvents.ShowError(errorMessage = it))
                                        }
                                    }

                                    Response.Loading -> {
                                        _eventFlow.emit(UIEvents.ShowLoadingBar)
                                    }

                                    is Response.Success -> {
                                        println("response.data: ${response.data}")
                                        _eventFlow.emit(UIEvents.ChatUsrCreatedSuccessFully)
                                        response.data?.let { messageUserList ->
                                            messageUserList.UserPersonalChatList?.let {
                                                it.keys.forEach{key->
                                                    println("key: $key")
                                                    println("value: ${it[key]}")
                                                    println("otherUserPublicUid: ${it[key]?.otherUserPublicUid}")
                                                    if (it[key]?.otherUserPublicUid.equals(publicUID)){
                                                        println("Check: Matched")
                                                        println("same UID found")
                                                    }
                                                }
                                            }
                                            println("check point1")
//                                            val map = messageUserList.UserPersonalChatList
                                            val userPersonalChatList: UserPersonalChatList? = messageUserList.UserPersonalChatList.let { map ->
                                                map?.entries?.map { entry ->
                                                    if (entry.value.otherUserPublicUid.equals(publicUID)) {
                                                        println("check point2")
                                                        UserPersonalChatList(
                                                            otherUserPublicUid = messageUserList.publicUid,
                                                            otherUserPublicUname = messageUserList.publicUname,
                                                            otherUserProfileUrl = messageUserList.profileUrl,
                                                            commonChatRoomId = entry.value.commonChatRoomId,
                                                            commonEncryptionKey = entry.value.commonEncryptionKey,
                                                            commonEncryptionIv = entry.value.commonEncryptionIv
                                                        )
                                                    } else {
                                                        println("check point3")
                                                        UserPersonalChatList(
                                                            otherUserPublicUid = messageUserList.publicUid,
                                                            otherUserPublicUname = messageUserList.publicUname,
                                                            otherUserProfileUrl = messageUserList.profileUrl,
                                                            commonChatRoomId = generateChatRoomId(messageUserList.publicUid)
                                                        )
                                                    }
                                                }?.firstOrNull() ?: UserPersonalChatList(
                                                    otherUserPublicUid = messageUserList.publicUid,
                                                    otherUserPublicUname = messageUserList.publicUname,
                                                    otherUserProfileUrl = messageUserList.profileUrl,
                                                    commonChatRoomId = generateChatRoomId(messageUserList.publicUid)
                                                )
                                            }


//                                            val userPersonalChatList =
//                                                if (map?.get(publicUID)?.otherUserPublicUid == publicUID) {
//                                                    UserPersonalChatList(
//                                                        otherUserPublicUid = messageUserList.publicUid,
//                                                        otherUserPublicUname = messageUserList.publicUname,
//                                                        otherUserProfileUrl = messageUserList.profileUrl,
//                                                        commonChatRoomId = map.get(publicUID)?.commonChatRoomId,
//                                                        commonEncryptionKey = map.get(publicUID)?.commonEncryptionKey,
//                                                        commonEncryptionIv = map.get(publicUID)?.commonEncryptionIv
//                                                    )
//                                                } else {
//                                                    UserPersonalChatList(
//                                                        otherUserPublicUid = messageUserList.publicUid,
//                                                        otherUserPublicUname = messageUserList.publicUname,
//                                                        otherUserProfileUrl = messageUserList.profileUrl,
//                                                        commonChatRoomId = generateChatRoomId(
//                                                            messageUserList.publicUid
//                                                        )
//                                                    )
//                                                }
                                            println("userPersonalChatList: $userPersonalChatList")
                                            chatUseCases.createChatUser(
                                                event.value,
                                                userPersonalChatList!!
                                            )
                                                .collect { response ->
                                                    when (response) {
                                                        is Response.Failure -> {
                                                            _eventFlow.emit(UIEvents.ChatUsrCreatedSuccessFully)
                                                        }

                                                        Response.Loading -> {


                                                        }

                                                        is Response.Success -> {

                                                        }
                                                    }

                                                }
//
                                        }
                                    }
                                }
                            }
                    }

                }
            }
        }
    }

    private fun generateChatRoomId(otherUserPublicUid: String?): String {
        println("$publicUID$otherUserPublicUid")
        return "$publicUID$otherUserPublicUid"
    }

    private fun validatePublicUID(value: String): String {
        return value.trim()
    }

    private fun getChatUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            chatUseCases.getChatUsers().collect { response ->
                withContext(Dispatchers.Main) {
                    when (response) {
                        is Response.Success<*, *> -> {
                            _state.value = state.value.copy(
                                chatUsersList = response.data as List<UserPersonalChatList>
                            )
                            println("Chat Users: ${response.data}")
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