package com.aman.keyswithkotlin.core

import com.aman.keyswithkotlin.di.AESKeySpecs
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AESKeySpacsProvider() {
    suspend operator fun invoke(UID: String, db: FirebaseDatabase): AESKeySpecs {
        val keys = AESKeySpecs()
        val aesKeyRef = db.reference.child("users").child(UID).child("aesKey")
        val aesIVRef = db.reference.child("users").child(UID).child("aesIV")

        val aesKeySnapshot = withContext(Dispatchers.Main) { aesKeyRef.get().await() }
        val aesIVSnapshot = withContext(Dispatchers.Main) { aesIVRef.get().await() }

        val aesKey = aesKeySnapshot.getValue(String::class.java)
        val aesIV = aesIVSnapshot.getValue(String::class.java)

        aesKey?.let {
            println("aesKey $it")
            keys.aesKey = it
        }

        aesIV?.let {
            println("aesIV $it")
            keys.aesIV = it
        }

        return keys
    }
}
