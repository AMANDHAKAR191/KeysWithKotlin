package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.Person
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList

data class SharedChatState(
    var person: UserPersonalChatList? = null,
//    var commonChatRoomId:String? = ""
)