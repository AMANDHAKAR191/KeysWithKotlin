package com.aman.keys.access_verification.data.repository

import com.aman.keys.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keys.auth.domain.model.DeviceData
import com.aman.keys.auth.domain.model.RequestAuthorizationAccess
import com.aman.keys.core.Authorization
import com.aman.keys.core.util.Response
import com.aman.keys.di.UID
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

    override fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<List<DeviceData>?, Boolean?>>> =
        callbackFlow {
            var deviceList = mutableListOf<DeviceData>()
            val reference = db.reference.child("users")
                .child(UID).child("userDevicesList")

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (ds in dataSnapshot.children) {
                            for (ds1 in ds.children) {
                                val item = convertDataSnapshotToDeviceData(ds1)
                                item?.let {
                                    deviceList.add(item)
                                }
                            }
                        }
                        trySend(Response.Success(deviceList))
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

    override fun removeAuthorizationAccessOfSecondaryDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            try {
                db.reference.child("users")
                    .child(UID).child("userDevicesList").child(deviceId).child(deviceId)
                    .child("authorization")
                    .setValue(Authorization.NotAuthorized.toString()).addOnCompleteListener {
                        trySend(Response.Success(data = "Access Removed"))
                    }
            } catch (e: Exception) {
                trySend(Response.Failure(e))
            }
            awaitClose {
                close()
            }
        }

    override fun giveAuthorizationAccessOfSecondaryDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            try {
                db.reference.child("users")
                    .child(UID).child("userDevicesList").child(deviceId).child(deviceId)
                    .child("authorization")
                    .setValue(Authorization.Authorized.toString()).addOnCompleteListener {
                        trySend(Response.Success(data = "Access Given"))
                    }
            } catch (e: Exception) {
                trySend(Response.Failure(e))
            }
            awaitClose {
                close()
            }
        }

    override fun requestAuthorizationAccess(
        primaryDeviceId: String,
        authorizationCode:Int,
        requestingDeviceId: String
    ): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            val _requestAuthorizationAccess =
                RequestAuthorizationAccess(
                    requesterID = requestingDeviceId,
                    authorizationCode = authorizationCode,
                    requestingAccess = true
                )
            try {
                db.reference.child("users")
                    .child(UID).child("userDevicesList").child(primaryDeviceId)
                    .child(primaryDeviceId)
                    .child("requestAuthorizationAccess").setValue(_requestAuthorizationAccess)
                    .addOnCompleteListener {
                        trySend(Response.Success(status = true))
                    }
            } catch (e: Exception) {
                trySend(Response.Failure(e))
            }
            awaitClose {
                close()
            }
        }

    override fun completeAuthorizationAccessProcess(primaryDeviceId: String): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {

            val _requestAuthorizationAccess =
                RequestAuthorizationAccess(
                    requesterID = "",
                    authorizationCode = 0,
                    requestingAccess = false
                )
            try {
                db.reference.child("users")
                    .child(UID).child("userDevicesList").child(primaryDeviceId)
                    .child(primaryDeviceId)
                    .child("requestAuthorizationAccess").setValue(_requestAuthorizationAccess)
                    .addOnCompleteListener {
                        trySend(Response.Success(status = true))
                    }
            } catch (e: Exception) {
                trySend(Response.Failure(e))
            }
            awaitClose {
                close()
            }
        }

    override fun getAccessRequesterClient(deviceId: String): Flow<Response<Pair<RequestAuthorizationAccess?, Boolean?>>> =
        callbackFlow {
            val reference = db.reference.child("users")
                .child(UID).child("userDevicesList").child(deviceId).child(deviceId)
                .child("requestAuthorizationAccess")
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val item = dataSnapshot.getValue(RequestAuthorizationAccess::class.java)
                    item?.let {
                        trySend(Response.Success(it))
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

    private fun convertDataSnapshotToDeviceData(dataSnapshot: DataSnapshot): DeviceData? {
        // Conversion logic here
        return dataSnapshot.getValue(DeviceData::class.java)
    }
}