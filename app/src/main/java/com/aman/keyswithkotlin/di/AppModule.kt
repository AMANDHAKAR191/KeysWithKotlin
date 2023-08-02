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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    suspend fun providePublicID(
        database: FirebaseDatabase,
        UID: String,
    ): String {
        val publicUidRef = database.reference.child("users").child(UID).child("publicUID")
        val publicUidSnapshot = withContext(Dispatchers.Main) { publicUidRef.get().await() }
        val publicID = publicUidSnapshot.getValue(String::class.java)
        println("publicUID1: $publicID")
        return publicID!!
    }

    @Provides
    fun provideAESKeySpacs(): AESKeySpecs =
        AESKeySpecs("Xv6mxim2Blr58AzECQxQbz==", "Ou8n2PI2X4mJc4m9Zx3Ljb")

//    todo resolve this issue
//    @Provides
//    suspend fun provideAESKeySpacs(
//        UID: String,
//        db: FirebaseDatabase,
//        aesKeySpacsProvider: AESKeySpacsProvider
//    ): AESKeySpecs {
//        return aesKeySpacsProvider.invoke(UID, db)
//    }
}

    data class AESKeySpecs(
        var aesKey: String = "",
        var aesIV: String = "",
    )