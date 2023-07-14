package com.aman.keyswithkotlin.chats.domain.use_cases

import com.aman.keyswithkotlin.chats.domain.model.Chat
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

class SendMessage(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chat: Chat): Flow<Response<Pair<MutableList<String>?, Boolean?>>> {
        return chatRepository.sendMessage(chat)
    }
}