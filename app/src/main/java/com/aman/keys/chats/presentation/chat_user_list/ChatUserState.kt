package com.aman.keys.chats.presentation.chat_user_list

import com.aman.keys.chats.domain.model.UserPersonalChatList

data class ChatUserState (
    var username:String = "",
    var chatUsersList:List<UserPersonalChatList>? = emptyList(),
    val error:String?  = "",
    val isLoading:Boolean = false,
)