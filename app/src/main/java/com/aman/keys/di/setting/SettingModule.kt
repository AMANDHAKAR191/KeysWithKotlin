package com.aman.keys.di.setting

import androidx.compose.animation.ExperimentalAnimationApi
import com.aman.keys.core.MyPreference
import com.aman.keys.di.AESKeySpecs
import com.aman.keys.di.AES_CLOUD_KEY_SPECS
import com.aman.keys.di.AES_LOCAL_KEY_SPECS
import com.aman.keys.di.PublicUID
import com.aman.keys.di.UID
import com.aman.keys.setting.data.repository.SettingRepositoryImpl
import com.aman.keys.setting.domain.repository.SettingRepository
import com.aman.keys.setting.domain.use_cases.SettingUseCases
import com.aman.keys.setting.domain.use_cases.StoreImportedPasswords
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