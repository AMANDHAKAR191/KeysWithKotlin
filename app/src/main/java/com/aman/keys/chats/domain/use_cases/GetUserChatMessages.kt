package com.aman.keys.chats.domain.use_cases

import com.aman.keys.chats.domain.repository.ChatRepository
import com.aman.keys.chats.domain.repository.GetUserChatMessagesResponse
import kotlinx.coroutines.flow.Flow

class GetUserChatMessages(
    private val repository: ChatRepository
) {
    operator fun invoke(chatRoomId:String): Flow<GetUserChatMessagesResponse> {
        return repository.getUserChatMessages(chatRoomId)
    }
}