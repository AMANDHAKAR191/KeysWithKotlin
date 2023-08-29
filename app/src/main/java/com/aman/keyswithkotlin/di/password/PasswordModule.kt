package com.aman.keyswithkotlin.di.password

import androidx.compose.animation.ExperimentalAnimationApi
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.aman.keyswithkotlin.di.AES_CLOUD_KEY_SPECS
import com.aman.keyswithkotlin.di.AES_LOCAL_KEY_SPECS
import com.aman.keyswithkotlin.di.PublicUID
import com.aman.keyswithkotlin.di.UID
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
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
        @PublicUID
        publicUID: String,
        myPreference: MyPreference
    ): PasswordUseCases {

        return PasswordUseCases(
            getPasswords = GetPasswords(repository, aesCloudKeySpecs, aesLocalKeySpecs),
            getRecentlyUsedPasswords = GetRecentlyUsedPasswords(repository, aesCloudKeySpecs, aesLocalKeySpecs),
            updateLastUsedPasswordTimeStamp = UpdateLastUsedPasswordTimeStamp(repository),
            addPassword = AddPassword(repository, aesCloudKeySpecs, aesLocalKeySpecs),
            deletePassword = DeletePassword(repository),
            generatePassword = GeneratePassword(repository),
            saveRecentGeneratedPassword = SaveRecentGeneratedPassword(repository),
            getRecentGeneratedPasswords = GetRecentGeneratedPasswords(repository)
        )
    }



    @Provides
    fun providePasswordRepository(
        database: FirebaseDatabase,
        @UID
        UID: String,
        myPreference: MyPreference
    ): PasswordRepository {
        return PasswordRepositoryImpl(database, UID = UID, myPreference)
    }
}