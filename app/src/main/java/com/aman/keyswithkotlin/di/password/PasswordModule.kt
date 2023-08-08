package com.aman.keyswithkotlin.di.password

import androidx.compose.animation.ExperimentalAnimationApi
import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.passwords.data.repository.PasswordRepositoryImpl
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.passwords.domain.use_cases.AddPassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.DeletePassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.GeneratePassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.GetPasswords
import com.aman.keyswithkotlin.passwords.domain.use_cases.GetRecentGeneratedPasswords
import com.aman.keyswithkotlin.passwords.domain.use_cases.GetRecentlyUsedPasswords
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import com.aman.keyswithkotlin.passwords.domain.use_cases.SaveRecentGeneratedPassword
import com.aman.keyswithkotlin.passwords.domain.use_cases.UpdateLastUsedPasswordTimeStamp
import com.aman.keyswithkotlin.presentation.MainActivity
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class PasswordModule {
    @OptIn(ExperimentalAnimationApi::class)
    @Provides
    fun providePasswordUseCases(
        repository: PasswordRepository,
        aesKeySpecs: AESKeySpecs,
        publicUID:String,
        myPreference: MyPreference
    ): PasswordUseCases {
        println("providePasswordUseCases:publicUID:: $publicUID")

        println("myPreference.AES_KEY: ${myPreference.sharedPreferences.getString(myPreference.AES_KEY, "Hello")}")

        return PasswordUseCases(
            getPasswords = GetPasswords(repository, aesKeySpecs),
            getRecentlyUsedPasswords = GetRecentlyUsedPasswords(repository ,aesKeySpecs),
            updateLastUsedPasswordTimeStamp = UpdateLastUsedPasswordTimeStamp(repository),
            addPassword = AddPassword(repository, aesKeySpecs),
            deletePassword = DeletePassword(repository),
            generatePassword = GeneratePassword(repository),
            saveRecentGeneratedPassword =  SaveRecentGeneratedPassword(repository),
            getRecentGeneratedPasswords =  GetRecentGeneratedPasswords(repository),
        )
    }

    @Provides
    fun providePasswordRepository(
        database: FirebaseDatabase,
        UID: String
    ): PasswordRepository {
        return PasswordRepositoryImpl(database, UID = UID)
    }
}