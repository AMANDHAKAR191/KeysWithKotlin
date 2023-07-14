package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.Person

sealed class ChatEvent {
    data class OpenChat(val person: Person) : ChatEvent()
    object resetSharedViewModel : ChatEvent()
}