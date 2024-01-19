package com.aman.keys.di.chat

import com.aman.keys.chats.data.repository.ChatRepositoryImpl
import com.aman.keys.chats.domain.repository.ChatRepository
import com.aman.keys.chats.domain.use_cases.ChatUseCases
import com.aman.keys.chats.domain.use_cases.CreateChatUser
import com.aman.keys.chats.domain.use_cases.CreateUserInReceiverChat
import com.aman.keys.chats.domain.use_cases.GetChatProfileDataByPublicUID
import com.aman.keys.chats.domain.use_cases.GetChatUsers
import com.aman.keys.chats.domain.use_cases.GetUserChatMessages
import com.aman.keys.chats.domain.use_cases.SendMessage
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.di.AES_CLOUD_KEY_SPECS
import com.aman.keys.di.AES_LOCAL_KEY_SPECS
import com.aman.keys.di.PublicUID
import com.aman.keys.di.UID
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
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
    ): ChatUseCases {
        return ChatUseCases(
            sendMessage = SendMessage(repository),
            getChatUsers = GetChatUsers(repository),
            getUserChatMessages = GetUserChatMessages(repository),
            createChatUser = CreateChatUser(repository),
            createUserInReceiverChat = CreateUserInReceiverChat(repository),
            getChatProfileDataByPublicUID = GetChatProfileDataByPublicUID(repository)
        )
    }
//    @Provides
//    @SharedChat
//    fun provideSharedViewModel(
//        myPreference: MyPreference
//    ): SharedChatViewModel {
//        return SharedChatViewModel(myPreference)
//    }

    @Provides
    fun provideChatRepository(
        database: FirebaseDatabase,
        @UID
        UID: String,
        @PublicUID
        publicUID: String,
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
    ): ChatRepository {
        return ChatRepositoryImpl(database,UID = UID, publicUID, aesCloudKeySpecs)
    }
}
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class SharedChat