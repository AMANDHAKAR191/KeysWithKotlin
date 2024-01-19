package com.aman.keys.chats.domain.use_cases

import com.aman.keys.chats.domain.repository.ChatRepository
import com.aman.keys.chats.domain.repository.GetChatUsersResponse
import kotlinx.coroutines.flow.Flow

class GetChatUsers(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<GetChatUsersResponse> {
        return repository.getChatUsers()
    }
}