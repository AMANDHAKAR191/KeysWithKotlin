package com.aman.keys.setting.data.repository

import com.aman.keys.core.util.Response
import com.aman.keys.passwords.domain.model.Password
import com.aman.keys.setting.domain.repository.SettingRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingRepositoryImpl(
    private val database: FirebaseDatabase,
    private val UID: String,
) : SettingRepository {
    override fun storeImportedPasswords(passwordList: List<Password>): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            println("storeImportedPasswords: check5")

            val reference = database.reference.child("Passwords").child(UID)
            val passwordMap = HashMap<String, Password>()

            for (passwordModel in passwordList) {
                val uniqueKey = reference.push().key

                if (uniqueKey != null) {
                    passwordMap[uniqueKey] = passwordModel
                }
            }
            trySend(Response.Loading(message = null))
            try {
                reference.updateChildren(passwordMap.toMap())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(Response.Success("Password is successfully saved"))
                        }
                    }.addOnFailureListener {
                        trySend(Response.Failure(it))
                    }
                awaitClose {
                    close()
                }
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
}