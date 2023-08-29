package com.aman.keyswithkotlin.di.chat

import com.aman.keyswithkotlin.chats.data.repository.ChatRepositoryImpl
import com.aman.keyswithkotlin.chats.domain.repository.ChatRepository
import com.aman.keyswithkotlin.chats.domain.use_cases.ChatUseCases
import com.aman.keyswithkotlin.chats.domain.use_cases.CreateChatUser
import com.aman.keyswithkotlin.chats.domain.use_cases.GetChatProfileDataByPublicUID
import com.aman.keyswithkotlin.chats.domain.use_cases.GetChatUsers
import com.aman.keyswithkotlin.chats.domain.use_cases.GetUserChatMessages
import com.aman.keyswithkotlin.chats.domain.use_cases.SendMessage
import com.aman.keyswithkotlin.chats.presentation.SharedChatViewModel
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.di.AES_CLOUD_KEY_SPECS
import com.aman.keyswithkotlin.di.AES_LOCAL_KEY_SPECS
import com.aman.keyswithkotlin.di.PublicUID
import com.aman.keyswithkotlin.di.UID
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier
import javax.inject.Singleton

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
        @PublicUID
        publicUID: String,
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
    ): ChatRepository {
        return ChatRepositoryImpl(database, publicUID, aesCloudKeySpecs)
    }
}
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class SharedChat