package com.aman.keyswithkotlin.di

import com.aman.keyswithkotlin.core.AESKeySpacsProvider
import com.aman.keyswithkotlin.core.MyPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

    @Provides
    @UID
    fun provideUID() = Firebase.auth.currentUser!!.uid

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
    fun provideAESKeySpacs(): AESKeySpecs =
        AESKeySpecs("Xv6mxim2Blr58AzECQxQbz==", "Ou8n2PI2X4mJc4m9Zx3Ljb")


}

data class AESKeySpecs(
    var aesKey: String = "",
    var aesIV: String = "",
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UID

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicUID