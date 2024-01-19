package com.aman.keys.chats.domain.use_cases

import com.aman.keys.chats.domain.repository.ChatRepository
import com.aman.keys.chats.domain.repository.GetChatProfileDataResponse
import kotlinx.coroutines.flow.Flow

class GetChatProfileDataByPublicUID (
    private val repository: ChatRepository
) {
    operator fun invoke(otherUserPublicUid:String): Flow<GetChatProfileDataResponse> {
        return repository.getChatProfileDataByPublicUID(otherUserPublicUid)
    }
}