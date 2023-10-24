package com.aman.keyswithkotlin

import android.app.Application
import android.system.Os.close
import com.aman.keyswithkotlin.core.util.Response
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltAndroidApp
class Keys : Application() {
    companion object {
        lateinit var instance: Keys
            private set
        var web_client_id:String = "default_web_client_id"
            private set
        var fcm_server_key:String = "default_web_client_id"
            private set
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        instance = this
        Firebase.database.setPersistenceEnabled(true)
        // Retrieve server_key here
        GlobalScope.launch {
            retrieveWebClientID().collectLatest {
                web_client_id = it
            }
            retrieveFCMServerKey().collectLatest {
                fcm_server_key = it
            }
        }
    }

    private fun retrieveWebClientID(): Flow<String> = callbackFlow {
        val query = FirebaseDatabase.getInstance().reference.child("web_client_id")
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val webClientID = dataSnapshot.getValue(String::class.java)!!
                    trySend(webClientID)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }
        query.addValueEventListener(listener)
        awaitClose {
            close()
            query.removeEventListener(listener)
        }
    }
    private fun retrieveFCMServerKey(): Flow<String> = callbackFlow {
        val query = FirebaseDatabase.getInstance().reference.child("fcm_server_key")
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val fcmServerKey = dataSnapshot.getValue(String::class.java)!!
                    trySend(fcmServerKey)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        }
        query.addValueEventListener(listener)
        awaitClose {
            close()
            query.removeEventListener(listener)
        }
    }
}