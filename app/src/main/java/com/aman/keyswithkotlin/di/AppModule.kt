package com.aman.keyswithkotlin.di

import com.aman.keyswithkotlin.core.AESKeySpacsProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

    @Provides
    fun provideUID() = Firebase.auth.currentUser!!.uid

    @Provides
    fun provideFirebaseDatabase() = Firebase.database

    @Provides
    fun provideAESProvider() = AESKeySpacsProvider()

    @Provides
    fun provideAESKeySpacs():AESKeySpacs = AESKeySpacs("Xv6mxim2Blr58AzECQxQbz==","Ou8n2PI2X4mJc4m9Zx3Ljb")

    //todo resolve this issue
//    @Provides
//    fun provideAESKeySpacs(
//        UID: String,
//        db: FirebaseDatabase,
//        aesKeySpacsProvider: AESKeySpacsProvider
//    ): AESKeySpacs {
//        println("check1")
//        return aesKeySpacsProvider(UID, db)
//    }
}

data class AESKeySpacs(
    var aesKey: String = "",
    var aesIV: String = "",
)