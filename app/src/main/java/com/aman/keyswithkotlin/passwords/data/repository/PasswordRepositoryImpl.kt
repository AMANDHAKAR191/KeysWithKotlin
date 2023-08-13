package com.aman.keyswithkotlin.passwords.data.repository

import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.aman.keyswithkotlin.passwords.domain.model.GeneratedPasswordModelClass
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
    private val database: FirebaseDatabase,
    private val UID: String,
    private val myPreference: MyPreference
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
                    for (ds in dataSnapshot.children) {
                        for (ds1 in ds.children) {
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

    override fun getRecentlyUsedPasswords(): Flow<Response<Pair<MutableList<Password>?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("Passwords").child(UID)
            reference.keepSynced(true)
            trySend(Response.Loading)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _passwordsItems.clear()
                    for (ds in dataSnapshot.children) {
                        for (ds1 in ds.children) {
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
        val timeStampUtil = TimeStampUtil()

        val reference = database.reference.child("Passwords").child(UID)
        trySend(Response.Loading)
        val _password = Password(
            userName = password.userName,
            password = password.password,
            websiteName = password.websiteName,
            websiteLink = password.websiteLink,
            timestamp = timeStampUtil.generateTimestamp()
        )
        try {
            reference.child(password.websiteName).child(_password.timestamp).setValue(_password)
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

    override fun deletePassword(password: Password): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            val reference = database.reference.child("Passwords").child(UID)
            trySend(Response.Loading)
            reference.child(password.websiteName).orderByChild("timestamp")
                .equalTo(password.timestamp)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            val noteKey = childSnapshot.key
                            println("noteKey: $noteKey")
                            reference.child(password.websiteName).child(noteKey!!).removeValue()
                                .addOnCompleteListener {
                                    println("password is successfully deleted")
                                    if (it.isSuccessful) {
                                        println("password is successfully deleted1")
                                        trySend(Response.Success(data = "password is successfully deleted"))
                                    }
                                }.addOnFailureListener {
                                    trySend(Response.Failure(it))
                                }
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

    override fun saveGeneratedPassword(
        generatePassword: String, recentPasswordsList: MutableList<GeneratedPasswordModelClass>
    ): Flow<Response<Pair<String?, Boolean?>>> = callbackFlow {
        trySend(Response.Loading)
        // Add the new password to the beginning of the list
        val recentPassword = GeneratedPasswordModelClass(generatePassword, recentPasswordsList.size)
        recentPasswordsList.add(0, recentPassword)
        recentPassword.passwordCount = 0

        // Update the passwordCount for all the passwords
        for (i in recentPasswordsList.indices) {
            recentPasswordsList[i].passwordCount = i
        }

//            // Remove the oldest password from the end if the list size exceeds 10
//            if (recentPasswordsList.size > 10) {
//                recentPasswordsList.removeAt(recentPasswordsList.size - 1)
//            }

        // TODO: Update the Firebase real-time database with the updated list of passwords
        database.reference.child("RecentGeneratedPasswords").child(UID)
            .setValue(recentPasswordsList).addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(Response.Success(data = "done"))
                } else {
                    trySend(Response.Failure(Throwable("Failed to add")))
                }
            }

        awaitClose {
            close()
        }
    }

    override fun getRecentGeneratedPasswords(): Flow<Response<Pair<MutableList<GeneratedPasswordModelClass>?, Boolean?>>> =
        callbackFlow {
            val reference = database.getReference("RecentGeneratedPasswords").child(UID)
            val passwordList = mutableListOf<GeneratedPasswordModelClass>()

            val listner = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val items = ds.getValue(GeneratedPasswordModelClass::class.java)
                        if (items != null) {
                            println("items: $items")
                            passwordList.add(items)
                        }
                    }
                    trySend(Response.Success(data = passwordList, true))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }

            reference.addValueEventListener(listner)
            awaitClose {
                close()
                reference.removeEventListener(listner)
            }
        }

    override fun updateRecentUsedPasswordTimeStamp(password: Password): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            val timeStampUtil = TimeStampUtil()
            database.reference.child("Passwords").child(UID).child(password.websiteName)
                .child(password.timestamp).child("lastUsedTimeStamp")
                .setValue(timeStampUtil.generateTimestamp()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(Response.Success(data = "Done", true))
                    } else {
                        trySend(Response.Failure(Throwable("Failed")))
                    }
                }.addOnFailureListener {
                    trySend(Response.Failure(it))
                }
            awaitClose {
                close()
            }
        }

//    override fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>> =
//        callbackFlow {
//            val reference = database.reference.child("users")
//                .child(UID).child("userDevicesList").child(deviceId).child("isAuthorize")
//            val listener = object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val item = dataSnapshot.getValue(String::class.java)
//                    item?.let {
//                        if (it == Authorization.Authorize.toString()) {
//                            trySend(Response.Success(Authorization.Authorize.toString()))
//                        } else {
//                            trySend(Response.Success(Authorization.NotAuthorize.toString()))
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    trySend(Response.Failure(error.toException()))
//                }
//            }
//            reference.addValueEventListener(listener)
//            awaitClose {
//                close()
//                reference.removeEventListener(listener)
//            }
//        }

}