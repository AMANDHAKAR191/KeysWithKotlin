package com.aman.keyswithkotlin.access_verification.data.repository

import com.aman.keyswithkotlin.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.UID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AccessVerificationRepositoryImpl(
    private val db: FirebaseDatabase,
    @UID private val UID: String,
) : AccessVerificationRepository {

    override fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            println("check4")
            val reference = db.reference.child("users")
                .child(UID).child("userDevicesList").child(deviceId).child("authorization")
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    println("dataSnapshot11: $dataSnapshot")
                    val item = dataSnapshot.getValue(String::class.java)
                    println("item: $item")
                    item?.let {
                        println("item: $it")
                        if (it == Authorization.Authorized.toString()) {
                            trySend(Response.Success(Authorization.Authorized.toString()))
                        } else {
                            trySend(Response.Success(Authorization.NotAuthorized.toString()))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Failure(error.toException()))
                }
            }
            reference.addValueEventListener(listener)
            awaitClose {
                close()
                reference.removeEventListener(listener)
            }
        }
}