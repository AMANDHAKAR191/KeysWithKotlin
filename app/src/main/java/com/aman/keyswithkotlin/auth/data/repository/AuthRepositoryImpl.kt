package com.aman.keyswithkotlin.auth.data.repository

import com.aman.keyswithkotlin.Keys
import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.model.User
import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.auth.domain.repository.OneTapSignInResponse
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.chats.domain.model.MessageUserList
import com.aman.keyswithkotlin.core.AES
import com.aman.keyswithkotlin.core.Authorization
import com.aman.keyswithkotlin.core.Constants.SIGN_IN_REQUEST
import com.aman.keyswithkotlin.core.Constants.SIGN_UP_REQUEST
import com.aman.keyswithkotlin.core.Constants.USERS
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.TimeStampUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Random
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    private var googleSignInClient: GoogleSignInClient,
    @Named(SIGN_IN_REQUEST) private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST) private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseDatabase,
    private val UID: String,
    private val myPreference: MyPreference
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null
    private val _userData: User? = null

    override val displayName = auth.currentUser?.displayName.toString()
    override val photoUrl = auth.currentUser?.photoUrl.toString()


    override suspend fun oneTapSignInWithGoogle(): Flow<OneTapSignInResponse> = callbackFlow {
        try {
            trySend(Response.Loading)
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            trySend(Response.Success(signInResult, true))
        } catch (e: Exception) {
            try {
                trySend(Response.Loading)
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                trySend(Response.Success(signUpResult, true))
            } catch (e: Exception) {
                trySend(Response.Failure(e))
            }
        }
        awaitClose { close() }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleAuthCredential: AuthCredential
    ): Flow<SignInWithGoogleResponse> = callbackFlow {

        try {
            val authResult = auth.signInWithCredential(googleAuthCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

            println("isNewUser: $isNewUser")

            coroutineScope {
                val addUserDeferred = async { addUserToFireBase(auth.currentUser) }
                val addDeviceDataDeferred = async { addDeviceDataToFireBase(auth.currentUser) }
                val getUserDeferred = async { getUserFromFireBase(auth.currentUser) }

                val status = if (isNewUser) {
                    println("new user")
                    addUserDeferred.await()
                    addDeviceDataDeferred.await()
                } else {
                    println("old user")
                    getUserDeferred.await()
                    addDeviceDataDeferred.await()
                }
                status.let {
                    trySend(it)
                }
            }
        } catch (e: Exception) {
            println("Error1: ${e.message}\n${e.printStackTrace()}")
            trySend(Response.Failure(e))
        }

        awaitClose { close() }
    }

    override suspend fun signOut(): Flow<SignOutResponse> = callbackFlow {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
            trySend(Response.Success(true))
        } catch (e: Exception) {
            trySend(Response.Failure(e))
        }
        awaitClose { close() }
    }

    override suspend fun revokeAccess(): Flow<RevokeAccessResponse> = callbackFlow {
        try {
            auth.currentUser?.apply {
//                db.reference.child(USERS).child(uid).removeValue().await()
                googleSignInClient.revokeAccess().await()
                oneTapClient.signOut().await()
                delete().await()
            }
            trySend(Response.Success(true))
        } catch (e: Exception) {
            trySend(Response.Failure(e))
        }
        awaitClose { close() }
    }

    override fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>> =
        callbackFlow {
            val reference = db.reference.child("users")
                .child(UID).child("userDevicesList").child(deviceId).child("isAuthorize")
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val item = dataSnapshot.getValue(Boolean::class.java)
                    item?.let {
                        if (it) {
                            trySend(Response.Success(Authorization.Authorize.toString()))
                        } else {
                            trySend(Response.Success(Authorization.NotAuthorize.toString()))
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

    private suspend fun addDeviceDataToFireBase(
        user: FirebaseUser?
    ): SignInWithGoogleResponse = coroutineScope {
        user?.let {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            return@coroutineScope try {
                db.reference.child(USERS).child(it.uid).child("userDevicesList")
                    .child(deviceInfo.getDeviceId()).setValue(getDeviceData(deviceInfo))
                    .await() // Wait for the first Firebase operation to complete
                Response.Success(status = true) // Return success response if both setValue operations are successful
            } catch (e: Exception) {
                Response.Failure(e) // Return failure response if either setValue operation fails
            }
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
    }

    private suspend fun addUserToFireBase(
        user: FirebaseUser?
    ): SignInWithGoogleResponse = coroutineScope {
        val aesKey = "${generatePassword(22, specialCharacters = false)}=="
        val aesIV = generatePassword(22, specialCharacters = false)
        user?.let {
            val timeStampUtil = TimeStampUtil()
            val newUser = User(
                displayName = it.displayName,
                email = it.email,
                photoUrl = it.photoUrl?.toString(),
                aesKey = aesKey,
                aesIV = aesIV,
                privateUID = it.uid,
                publicUID = createPublicUID(it.email),
                createdAt = timeStampUtil.generateTimestamp(),
                userDevicesList = null
            )

            AES.getInstance(aesKey, aesIV)?.let { aes ->
                val encryptedUser = encryptUser(newUser, aes)
                println("Adding user to Firebase...")

                return@coroutineScope try {
                    db.reference.child(USERS).child(it.uid).setValue(encryptedUser)
                        .await() // Wait for the first Firebase operation to complete

                    // After first setValue is successful, proceed to next setValue
                    val userListModel = MessageUserList(
                        publicUid = newUser.publicUID, publicUname = newUser.displayName, false
                    )
                    val referenceSender = db.reference
                    referenceSender.child("messageUserList").child(newUser.publicUID!!)
                        .setValue(userListModel).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
//                                Response.Success(status = true)
                                addUserInSharedPreferenceDB(
                                    newUser.aesKey, newUser.aesIV, newUser.publicUID!!
                                )
                            } else {
                                Response.Failure(Throwable("Unable to create account"))
                            }
                        }.await()// Wait for the second Firebase operation to complete

                    Response.Success(status = true) // Return success response if both setValue operations are successful
                } catch (e: Exception) {
                    Response.Failure(e) // Return failure response if either setValue operation fails
                }
            } ?: Response.Failure(Exception("AES initialization failed."))
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
    }

    private fun getDeviceData(deviceInfo: DeviceInfo): DeviceData {
        val timeStampUtil = TimeStampUtil()
        return DeviceData(
            deviceId = deviceInfo.getDeviceId(),
            deviceType = deviceInfo.getDeviceType(),
            isAuthorize = false,
            appVersion = deviceInfo.getAppVersion(),
            lastLoginTimeStamp = timeStampUtil.generateTimestamp(),
            ipAddress = "1234"
        )
    }

    private suspend fun getUserFromFireBase(
        user: FirebaseUser?
    ): SignInWithGoogleResponse = coroutineScope {
        user?.let {
            var _user: User? = null
            try {
                val reference = db.reference.child(USERS).child(it.uid)
                val listener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        _user = dataSnapshot.getValue(User::class.java)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the cancellation if needed
                        Response.Failure(error.toException())
                    }
                }
                reference.addListenerForSingleValueEvent(listener) // Perform the Firebase operation

                // Wait until the listener has updated the `_user` variable
                while (_user == null) {
                    kotlinx.coroutines.delay(100) // Add a small delay before checking again
                }
                println("User1: $_user")
                _user?.let {
                    Response.Success(
                        _user, true
                    ) // Return success response with the user object
                } ?: Response.Failure(Exception("User not found")) // Return failure response
            } catch (e: Exception) {
                Response.Failure(e) // Return failure response
            }
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
    }


//    private suspend fun addUserToFireBase(
//        user: FirebaseUser?
//    ): SignInWithGoogleResponse = coroutineScope {
//        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
//        val formatted = current.format(formatter)
//        val aesKey = "${generatePassword(22, specialCharacters = false)}=="
//        val aesIV = generatePassword(22, specialCharacters = false)
//        user?.let {
//            val newUser = User(
//                displayName = it.displayName,
//                email = it.email,
//                photoUrl = it.photoUrl?.toString(),
//                aesKey = aesKey,
//                aesIV = aesIV,
//                privateUID = it.uid,
//                publicUID = it.email
//            )
//
//            AES.getInstance(aesKey, aesIV)?.let { aes ->
//                val encryptedUser = encryptUser(newUser, aes)
//                println("Adding user0 to Firebase...")
//                try {
//                    db.reference.child(USERS).child(it.uid)
//                        .setValue(encryptedUser)
//                        .addOnCompleteListener {
//
//                            //check
//                            //create a personal user chat list
//                            val userListModel = MessageUserList(
//                                publicUid = newUser.publicUID,
//                                publicUname = newUser.displayName,
//                                false
//                            )
//                            val referenceSender = FirebaseDatabase.getInstance().reference
//                            referenceSender.child("messageUserList").child(newUser.publicUID!!)
//                                .setValue(userListModel)
//                                .addOnCompleteListener {
//                                    if (it.isSuccessful){
//
//                                    }
//                                }
//                            //check
//
//                        }.await() // Wait for the Firebase operation to complete
//
//                    Response.Success(status = true) // Return success response
//                } catch (e: Exception) {
//                    Response.Failure(e) // Return failure response
//                }
//            } ?: Response.Failure(Exception("AES initialization failed."))
//        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
//    }


    private fun addUserInSharedPreferenceDB(aesKey: String?, aesIV: String?, publicUID: String) {
        val editor = myPreference.sharedPreferences.edit()
        editor.putString(myPreference.AES_KEY, aesKey)
        editor.putString(myPreference.AES_IV, aesIV)
        editor.putString(myPreference.PUBLIC_UID, publicUID)
        editor.apply()
    }

    private suspend fun getUserFromFireBaseAlter(user: FirebaseUser?): SignInWithGoogleResponse {
        return coroutineScope {
            user?.let {
                var _user: User? = null
                try {
                    val reference = db.reference.child(USERS).child(it.uid)
                    val listener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            _user = dataSnapshot.getValue(User::class.java)
                            println("User: $_user")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Response.Failure(error.toException())
                            _user = null
                        }
                    }
                    reference.addListenerForSingleValueEvent(listener)
                    println("User1: $_user")
                    if (_user != null) {
                        Response.Success(
                            _user, true
                        ) // Return success response with the user object
                    } else {
                        Response.Failure(Exception("User not found")) // Return failure response
                    }
                } catch (e: Exception) {
                    Response.Failure(e) // Return failure response
                }
            } ?: Response.Failure(Exception("Invalid FirebaseUser."))
        }
    }


    private fun encryptUser(user: User, aes: AES): User {
        val encryptedUser = user.copy()
        encryptedUser.displayName = aes.singleEncryption(user.displayName!!)
        encryptedUser.email = aes.singleEncryption(user.email!!)
        encryptedUser.photoUrl = aes.singleEncryption(user.photoUrl!!)
        // Encrypt any other user properties as needed

        return encryptedUser
    }


    private fun createPublicUID(email: String?): String? {
        return email?.substringBeforeLast("@")
    }


    private fun generatePassword(
        max_length: Int,
        upperCase: Boolean = true,
        lowerCase: Boolean = true,
        numbers: Boolean = true,
        specialCharacters: Boolean = true
    ): String {
        val rn = Random()
        val sb = StringBuilder(max_length)
        try {
            val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
            val numberChars = "0123456789"
            val specialChars = "!@#$%^&*()_-+=<>?/{}~|"
            var allowedChars = ""


            //this will fulfill the requirements of atleast one character of a type.
            if (upperCase) {
                allowedChars += upperCaseChars
                sb.append(upperCaseChars[rn.nextInt(upperCaseChars.length - 1)])
            }
            if (lowerCase) {
                allowedChars += lowerCaseChars
                sb.append(lowerCaseChars[rn.nextInt(lowerCaseChars.length - 1)])
            }
            if (numbers) {
                allowedChars += numberChars
                sb.append(numberChars[rn.nextInt(numberChars.length - 1)])
            }
            if (specialCharacters) {
                allowedChars += specialChars
                sb.append(specialChars[rn.nextInt(specialChars.length - 1)])
            }
            //fill the allowed length from different chars now.
            for (i in sb.length until max_length) {
                sb.append(allowedChars[rn.nextInt(allowedChars.length)])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }


}
