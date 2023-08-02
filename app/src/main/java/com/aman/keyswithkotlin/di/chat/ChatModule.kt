package com.aman.keyswithkotlin.di.chat

import com.aman.keyswithkotlin.chats.data.ChatRepositoryImpl
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.chats.domain.use_cases.GetChatUsers
import com.aman.keyswithkotlin.chats.domain.use_cases.GetUserChatMessages
import com.aman.keyswithkotlin.chats.domain.use_cases.SendMessage
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.notes.data.repository.NoteRepositoryImpl
import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import com.aman.keyswithkotlin.notes.domain.use_cases.AddNote
import com.aman.keyswithkotlin.notes.domain.use_cases.DeleteNote
import com.aman.keyswithkotlin.notes.domain.use_cases.GetNotes
import com.aman.keyswithkotlin.notes.domain.use_cases.NoteUseCases
import com.aman.keyswithkotlin.notes.domain.use_cases.ShareNote
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ChatModule {


    @Provides
    fun provideChatUseCases(
        repository: ChatRepository,
        aesKeySpecs: AESKeySpecs
    ): ChatUseCases {
        return ChatUseCases(
            sendMessage = SendMessage(repository),
            getChatUsers = GetChatUsers(repository),
            getUserChatMessages = GetUserChatMessages(repository)
        )
    }

    @Provides
    fun provideChatRepository(
        database: FirebaseDatabase,
        UID: String,
        publicID:String,
        aesKeySpecs: AESKeySpecs
    ): ChatRepository {
        println("publicUID: $publicID")
        return ChatRepositoryImpl(database, UID = UID,"kirandhaker123", aesKeySpecs)
    }
}