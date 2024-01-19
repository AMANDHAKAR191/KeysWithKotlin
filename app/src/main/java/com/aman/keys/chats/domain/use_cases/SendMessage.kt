package com.aman.keys.chats.domain.use_cases

import com.aman.keys.chats.domain.model.ChatModelClass
import com.aman.keys.chats.domain.repository.ChatRepository
import com.aman.keys.chats.domain.repository.SendMessageResponse
import kotlinx.coroutines.flow.Flow

class SendMessage(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId:String, chat: ChatModelClass): Flow<SendMessageResponse> {
        return chatRepository.sendMessage(chatRoomId, chat)
    }
}