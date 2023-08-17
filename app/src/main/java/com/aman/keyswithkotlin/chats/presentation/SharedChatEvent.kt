package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.Person
import com.aman.keyswithkotlin.chats.domain.model.UserPersonalChatList

sealed class SharedChatEvent {
    data class OpenSharedChat(val person: UserPersonalChatList, val chatRoomId:String) : SharedChatEvent() }