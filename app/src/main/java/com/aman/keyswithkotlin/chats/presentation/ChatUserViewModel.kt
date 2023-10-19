package com.aman.keyswithkotlin.chats.presentation

import UIEvents
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TutorialType
import com.aman.keyswithkotlin.di.PublicUID
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordState
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatUserViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    @PublicUID
    private val publicUID: String,
    private val userName:String,
    private  val myPreference: MyPreference
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = MutableStateFlow(ChatUserState())
    val state= _state.asStateFlow()

    private val _isTutorialEnabled = MutableStateFlow(String())
    val isTutorialEnabled = _isTutorialEnabled.asStateFlow()



    init {
        _isTutorialEnabled.update { myPreference.isTutorialEnabled }
//        _state.value = state.value.copy(
//            username = userName
//        )
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
                                        _eventFlow.emit(UIEvents.ChatUsrCreatedSuccessFully)
                                        response.data?.let { messageUserList ->
                                            messageUserList.UserPersonalChatList?.let {
                                                it.keys.forEach{key->
                                                    if (it[key]?.otherUserPublicUid.equals(publicUID)){
                                                    }
                                                }
                                            }
                                            val userPersonalChatList: UserPersonalChatList? = messageUserList.UserPersonalChatList.let { map ->
                                                map?.entries?.map { entry ->
                                                    if (entry.value.otherUserPublicUid.equals(publicUID)) {
                                                        UserPersonalChatList(
                                                            otherUserPublicUid = messageUserList.publicUid,
                                                            otherUserPublicUname = messageUserList.publicUname,
                                                            otherUserProfileUrl = messageUserList.profileUrl,
                                                            commonChatRoomId = entry.value.commonChatRoomId,
                                                            commonEncryptionKey = entry.value.commonEncryptionKey,
                                                            commonEncryptionIv = entry.value.commonEncryptionIv
                                                        )
                                                    } else {
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

            ChatUserEvent.DisableTutorial -> {
                _isTutorialEnabled.update { myPreference.isTutorialEnabled }
                myPreference.isTutorialEnabled = TutorialType.DISABLED.toString()
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