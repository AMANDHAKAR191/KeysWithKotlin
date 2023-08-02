package com.aman.keyswithkotlin.chats.presentation

sealed class ChatEvent {
    data class OnMessageEntered(val value:String):ChatEvent()
    object SendMessage:ChatEvent()
}