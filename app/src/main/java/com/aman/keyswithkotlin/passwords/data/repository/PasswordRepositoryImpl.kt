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

class PasswordRepositoryImpl(
    private val database: FirebaseDatabase
) : PasswordRepository {

    private val _passwordsItems = mutableListOf<Password>()
    override fun getPasswords(): Flow<Response<Pair<MutableList<Password>?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("Passwords")
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _passwordsItems.clear()
                    println("dataSnapshot: $dataSnapshot")
                    for (ds in dataSnapshot.children) {
                        for (ds1 in ds.children){
                            val items = ds1.getValue(Password::class.java)
                            if (items != null) {
                                _passwordsItems.add(items)
                            }
                        }
                    }
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

override fun deletePassword(password: Password): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow {
    val reference = database.reference.child("Passwords")
    reference.keepSynced(true)
    trySend(Response.Loading)
    reference.child(password.websiteName).child(password.userName)
        .removeValue()
        .addOnCompleteListener {
            trySend(Response.Success(data = "Password is successfully deleted"))
        }
        .addOnFailureListener {
            trySend(Response.Failure(it))
        }
    awaitClose {
        close()
    }
}
}