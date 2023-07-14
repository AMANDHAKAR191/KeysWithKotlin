package com.aman.keyswithkotlin.passwords.data.repository

import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PasswordRepositoryImpl(
    private val database: FirebaseDatabase,
    private val UID: String
) : PasswordRepository {

    private val _passwordsItems = mutableListOf<Password>()


    override fun getPasswords(): Flow<Response<Pair<MutableList<Password>?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("Passwords").child(UID)
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _passwordsItems.clear()
                    println("dataSnapshot: $dataSnapshot")
                    for (ds in dataSnapshot.children) {
                        for (ds1 in ds.children) {
                            println("ds1: $ds")
                            val items = ds1.getValue(Password::class.java)
                            if (items != null) {
                                println("items: $items")
                                _passwordsItems.add(items)
                            }
                        }
                    }
                    println("passwords: $_passwordsItems")
                    trySend(Response.Success(data = _passwordsItems))

                }
                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Failure(error.toException()))
                }
            }

            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
                close()
            }
        }


    override fun insertPassword(
        password: Password
    ): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        val current = LocalDateTime.now()
        val formatted = current.format(formatter)

        val reference = database.reference.child("Passwords").child(UID)
        trySend(Response.Loading)
        val _password = Password(
            userName = password.userName,
            password = password.password,
            websiteName = password.websiteName,
            websiteLink = password.websiteLink,
            timestamp = formatted
        )
        try {
            println("password.timestamp :${password.timestamp}")
            reference.child(password.websiteName).child(_password.timestamp)
                .setValue(_password)
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
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun deletePassword(password: Password): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            println("inside: deletePassword")
            val reference = database.reference.child("Passwords").child(UID)
            trySend(Response.Loading)
            reference.child(password.websiteName).orderByChild("timestamp")
                .equalTo(password.timestamp)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val noteKey = childSnapshot.key
                            println("passwordKey: $noteKey")
                            reference.child(noteKey!!)
                                .removeValue()
                                .addOnCompleteListener {
                                    trySend(Response.Success(data = "password is successfully deleted"))
                                }
                                .addOnFailureListener {
                                    trySend(Response.Failure(it))
                                }
                            break
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        trySend(Response.Failure(databaseError.toException()))
                    }
                })
            awaitClose {
                close()
            }
        }
}