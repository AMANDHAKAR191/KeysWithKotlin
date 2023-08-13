package com.aman.keyswithkotlin.chats.domain.use_cases

data class ChatUseCases(
    val sendMessage: SendMessage,
    val getChatUsers: GetChatUsers,
    val getUserChatMessages: GetUserChatMessages,
    val createChatUser: CreateChatUser,
    val getChatProfileDataByPublicUID: GetChatProfileDataByPublicUID
)
