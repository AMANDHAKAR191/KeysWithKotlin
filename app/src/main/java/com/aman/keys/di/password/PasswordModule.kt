package com.aman.keys.di.password

import androidx.compose.animation.ExperimentalAnimationApi
import com.aman.keys.core.MyPreference
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.di.AES_CLOUD_KEY_SPECS
import com.aman.keys.di.AES_LOCAL_KEY_SPECS
import com.aman.keys.di.PublicUID
import com.aman.keys.di.UID
import com.aman.keys.passwords.data.repository.PasswordRepositoryImpl
import com.aman.keys.passwords.domain.repository.PasswordRepository
import com.aman.keys.passwords.domain.use_cases.AddPassword
import com.aman.keys.passwords.domain.use_cases.DeletePassword
import com.aman.keys.passwords.domain.use_cases.GeneratePassword
import com.aman.keys.passwords.domain.use_cases.GetPasswords
import com.aman.keys.passwords.domain.use_cases.GetRecentGeneratedPasswords
import com.aman.keys.passwords.domain.use_cases.GetRecentlyUsedPasswords
import com.aman.keys.passwords.domain.use_cases.PasswordUseCases
import com.aman.keys.passwords.domain.use_cases.SaveRecentGeneratedPassword
import com.aman.keys.passwords.domain.use_cases.UpdateLastUsedPasswordTimeStamp
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
        println("Aes keys${aesCloudKeySpecs.aesKey}")
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