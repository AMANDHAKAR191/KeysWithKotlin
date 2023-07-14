package com.aman.keyswithkotlin.chats.domain.repository

import com.aman.keyswithkotlin.chats.domain.model.Chat
import com.aman.keyswithkotlin.core.util.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun sendMessage(chat: Chat): Flow<Response<Pair<MutableList<String>?, Boolean?>>>
}