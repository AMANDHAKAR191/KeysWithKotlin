package com.aman.keyswithkotlin.chats.presentation

import com.aman.keyswithkotlin.chats.domain.model.Person

data class ChatState(
    var person: Person? = null
)