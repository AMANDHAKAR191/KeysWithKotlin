package com.aman.keyswithkotlin.passwords.data.repository

import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.model.RealtimeModelResponse
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.aman.keyswithkotlin.core.util.Response
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PasswordRepositoryImpl(
    private val database: FirebaseDatabase
) : PasswordRepository {

    private val _passwordsItems = mutableListOf<RealtimeModelResponse>()
    override suspend fun getPasswords(): Flow<Response<List<RealtimeModelResponse>>> =
        callbackFlow {
            val reference = database.reference.child("Passwords")
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _passwordsItems.clear()
                    println("Value Changed")
                    for (ds in dataSnapshot.children) {
                        println("ds: $ds")
                        val items = ds.children.map {
                            RealtimeModelResponse(
                                it.getValue(Password::class.java),
                                key = it.key
                            )
                        }
                        _passwordsItems.addAll(items)
                    }
                    trySend(Response.Success(_passwordsItems))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Failure(error.toException()))
                }

            }
            reference.addValueEventListener(listener)
            awaitClose {
                close()
            }
        }



    override suspend fun getPasswordById(id: Int): Password {
        TODO("Not yet implemented")
    }

    override suspend fun insertPassword(password: Password): Flow<Response<String>> = callbackFlow {
        val reference = database.reference.child("Passwords")
        reference.keepSynced(true)
        trySend(Response.Loading)
        reference.child(password.websiteName).child(password.userName)
            .setValue(password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(Response.Success("Password is successfully saved"))
                }
            }
            .addOnFailureListener {
                trySend(Response.Failure(it))
            }
        awaitClose {
            close()
        }
    }

    override suspend fun deletePassword(password: Password): Flow<Response<String>> = callbackFlow {
        val reference = database.reference.child("Passwords")
        reference.keepSynced(true)
        trySend(Response.Loading)
        reference.child(password.websiteName).child(password.userName)
            .removeValue()
            .addOnCompleteListener {
                trySend(Response.Success("Password is successfully deleted"))
            }
            .addOnFailureListener {
                trySend(Response.Failure(it))
            }
        awaitClose {
            close()
        }
    }
}