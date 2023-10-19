package com.aman.keyswithkotlin.di.setting

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
import com.aman.keyswithkotlin.setting.data.repository.SettingRepositoryImpl
import com.aman.keyswithkotlin.setting.domain.repository.SettingRepository
import com.aman.keyswithkotlin.setting.domain.use_cases.SettingUseCases
import com.aman.keyswithkotlin.setting.domain.use_cases.StoreImportedPasswords
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class SettingModule {
    @OptIn(ExperimentalAnimationApi::class)
    @Provides
    fun provideSettingUseCases(
        repository: SettingRepository,
        @AES_CLOUD_KEY_SPECS
        aesCloudKeySpecs: AESKeySpecs,
        @AES_LOCAL_KEY_SPECS
        aesLocalKeySpecs: AESKeySpecs,
        @PublicUID
        publicUID: String,
        myPreference: MyPreference
    ): SettingUseCases {

        return SettingUseCases(
            storeImportedPasswords = StoreImportedPasswords(repository, aesCloudKeySpecs, aesLocalKeySpecs)
        )
    }



    @Provides
    fun provideSettingRepository(
        database: FirebaseDatabase,
        @UID
        UID: String,
        myPreference: MyPreference
    ): SettingRepository {
        return SettingRepositoryImpl(database, UID = UID)
    }
}