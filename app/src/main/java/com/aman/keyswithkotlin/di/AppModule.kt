package com.aman.keyswithkotlin.di

import com.aman.keyswithkotlin.core.AESKeySpacsProvider
import com.aman.keyswithkotlin.core.Constants
import com.aman.keyswithkotlin.core.MyPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

    @Provides
    @UID
    fun provideUID() = Firebase.auth.currentUser!!.uid

    @Provides
    fun provideUserName() = Firebase.auth.currentUser!!.displayName?:"userName"

    @Provides
    fun provideFirebaseDatabase() = Firebase.database

    @Provides
    fun provideAESProvider() = AESKeySpacsProvider()

    @Provides
    fun provideSharedPreferenceProvider() = MyPreference()

    @Provides
    @PublicUID
    fun providePublicUID(
        auth: FirebaseAuth
    ): String {
        return auth.currentUser?.email?.split('@')?.get(0)!!
    }


    @Provides
    @AES_CLOUD_KEY_SPECS
    fun provideAESCloudKeySpecs(
        myPreference: MyPreference
    ): AESKeySpecs =
        AESKeySpecs(myPreference.aesCloudKey, myPreference.aesCloudIv)

    @Provides
    @AES_LOCAL_KEY_SPECS
    fun provideAESLocalKeySpecs(
        myPreference: MyPreference
    ): AESKeySpecs =
        AESKeySpecs(myPreference.aesCloudKey, myPreference.aesCloudIv)


}

data class AESKeySpecs(
    var aesKey: String = "",
    var aesIV: String = "",
)


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AES_CLOUD_KEY_SPECS
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AES_LOCAL_KEY_SPECS

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UID

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicUID