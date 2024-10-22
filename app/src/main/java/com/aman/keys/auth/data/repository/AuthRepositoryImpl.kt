package com.aman.keys.auth.data.repository

import com.aman.keys.Keys
import com.aman.keys.auth.domain.model.DeviceData
import com.aman.keys.auth.domain.model.User
import com.aman.keys.auth.domain.repository.AuthRepository
import com.aman.keys.auth.domain.repository.OneTapSignInResponse
import com.aman.keys.auth.domain.repository.RevokeAccessResponse
import com.aman.keys.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keys.auth.domain.repository.SignOutResponse
import com.aman.keys.chats.domain.model.MessageUserList
import com.aman.keys.core.AES
import com.aman.keys.core.Authentication
import com.aman.keys.core.Authorization
import com.aman.keys.core.Constants.SIGN_IN_REQUEST
import com.aman.keys.core.Constants.SIGN_UP_REQUEST
import com.aman.keys.core.Constants.USERS
import com.aman.keys.core.DeviceInfo
import com.aman.keys.core.DeviceType
import com.aman.keys.core.GeneratorClass
import com.aman.keys.core.MyPreference
import com.aman.keys.core.util.Response
import com.aman.keys.core.util.TimeStampUtil
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
    private val myPreference: MyPreference
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null
    private val _userData: User? = null

    override val displayName = auth.currentUser?.displayName.toString()
    override val email = auth.currentUser?.email.toString()
    override val photoUrl = auth.currentUser?.photoUrl.toString()


    override suspend fun oneTapSignInWithGoogle(): Flow<OneTapSignInResponse> = callbackFlow {
        try {
            trySend(Response.Loading(message = null))
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            trySend(Response.Success(signInResult, true))
        } catch (e: Exception) {
            try {
                trySend(Response.Loading(message = null))
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


            coroutineScope {
                checkFirebaseUser().collect { isUserExist ->
                    val status = if (!isUserExist) {    //new user
                        val addUserDeferred = async { addUserToFireBase(auth.currentUser) }
                        addUserDeferred.await()
                        async {
                            addDeviceDataToFireBase(
                                auth.currentUser, DeviceType.Primary, Authorization.Authorized
                            )
                        }.await()
                    } else { // old user
                        var deviceType:String = ""
                        async { checkPrimaryUser().collect{
                            deviceType = it
                        } }.await()
                        val getUserDeferred = async { getUserFromFireBase(auth.currentUser) }
                        getUserDeferred.await()
                        async {
                            if (deviceType == DeviceType.Primary.toString()){
                                addDeviceDataToFireBase(
                                    auth.currentUser, DeviceType.Primary, Authorization.Authorized
                                )
                            }else{
                                addDeviceDataToFireBase(
                                    auth.currentUser, DeviceType.Secondary, Authorization.NotAuthorized
                                )
                            }
                        }.await()
                    }
                    status.let {
                        trySend(it)
                    }
                }
            }
        } catch (e: Exception) {
            trySend(Response.Failure(e))
        }

        awaitClose { close() }
    }

    private suspend fun addDeviceDataToFireBase(
        user: FirebaseUser?, deviceType: DeviceType, authorization: Authorization
    ): SignInWithGoogleResponse = coroutineScope {
        user?.let {
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            val deviceId = deviceInfo.getDeviceId()
            val deviceData = mapOf(
                deviceId to getDeviceData(
                    deviceInfo,
                    deviceType,
                    authorization
                )
            )

            return@coroutineScope try {
                db.reference.child(USERS).child(it.uid).child("userDevicesList").child(deviceId)
                    .setValue(deviceData)
                    .await() // Update the specific device data under userDevicesList
                Response.Success(status = true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
    }

    private suspend fun addUserToFireBase(
        user: FirebaseUser?
    ): SignInWithGoogleResponse = coroutineScope {
//        Dh9sn08fI02mZfYFf3gNZO1NO1M60u/xgaIeqKVb5uo=

        val aesKey = "${GeneratorClass().generatePassword(43, specialCharacters = false)}="
        val aesIV = GeneratorClass().generatePassword(16, specialCharacters = false)
        user?.let {
            val timeStampUtil = TimeStampUtil()
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            val userDevicesList = mapOf(
                deviceInfo.getDeviceId() to getDeviceData(
                    deviceInfo = deviceInfo,
                    deviceType = DeviceType.Primary,
                    authorization = Authorization.Authorized
                )
            )

            val newUser = User(
                displayName = it.displayName,
                email = it.email,
                photoUrl = it.photoUrl?.toString(),
                aesKey = aesKey,
                aesIV = aesIV,
                privateUID = it.uid,
                publicUID = createPublicUID(it.email),
                createdAt = timeStampUtil.generateTimestamp(),
                userDevicesList = userDevicesList
            )
            //store aesKey in Keystore
//            addLocalSecretKeyInKeyStore(AES_ALIES_NAME, aesKey)


            AES.getInstance(aesKey, aesIV)?.let { aes ->
                val encryptedUser = encryptUser(newUser, aes)

                return@coroutineScope try {
                    println("check1")
                    db.reference.child(USERS).child(it.uid).setValue(encryptedUser)
                        .await() // Wait for the first Firebase operation to complete

                    // After first setValue is successful, proceed to next setValue
                    val userListModel = MessageUserList(
                        publicUid = newUser.publicUID,
                        publicUname = newUser.displayName,
                        false,
                        profileUrl = newUser.photoUrl
                    )
                    println("check1.1")
                    val referenceSender = db.reference
                    referenceSender.child("messageUserList").child(newUser.publicUID!!)
                        .setValue(userListModel).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                println("check1.1.1")
                                addUserInSharedPreferenceDB(
                                    newUser.aesKey, newUser.aesIV, newUser.publicUID!!
                                )
                            } else {
                                println("check1.1.2")
                                Response.Failure(Throwable("Unable to create account"))
                            }
                        }.await()// Wait for the second Firebase operation to complete
                    println("check1.2")
                    Response.Success(status = true) // Return success response if both setValue operations are successful
                } catch (e: Exception) {
                    println("check1.3")
                    Response.Failure(e) // Return failure response if either setValue operation fails
                }
            } ?: Response.Failure(Exception("AES initialization failed."))
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
    }

    private fun getDeviceData(
        deviceInfo: DeviceInfo,
        deviceType: DeviceType,
        authorization: Authorization
    ): DeviceData {
        val timeStampUtil = TimeStampUtil()
        return DeviceData(
            deviceId = deviceInfo.getDeviceId(),
            deviceName = deviceInfo.getDeviceName(),
            deviceBuildNumber = deviceInfo.getDeviceBuildNumber(),
            deviceType = deviceType.toString(),
            authorization = authorization.toString(),
            authentication = Authentication.Authenticated.toString(),
            appVersion = deviceInfo.getAppVersion(),
            lastLoginTimeStamp = timeStampUtil.generateTimestamp(),
            ipAddress = "1234"
        )
    }

    private fun checkFirebaseUser(): Flow<Boolean> = callbackFlow {
        auth.currentUser?.let { user ->
            val query = db.reference.child("users").child(user.uid)

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val item = dataSnapshot.getValue(String::class.java)
                    if (dataSnapshot.exists()) {
                        trySend(true) // Emitting true if the item is found
                    } else {
                        trySend(false) // Emitting false if the item is not found
                    }
                    close()
                }

                override fun onCancelled(error: DatabaseError) {
                    // You can handle the error here as you see fit
                }
            }
            query.addValueEventListener(listener)

            // Removing the listener when the flow is no longer collected
            awaitClose {
                query.removeEventListener(listener)
            }
        }
    }

    private suspend fun checkPrimaryUser(): Flow<String> = callbackFlow {
        auth.currentUser?.let { user ->
            val deviceInfo = DeviceInfo(Keys.instance.applicationContext)
            val query = db.reference.child("users").child(user.uid).child("userDevicesList")
                .child(deviceInfo.getDeviceId()).child(deviceInfo.getDeviceId())

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val authorization = dataSnapshot.child("authorization").getValue(String::class.java)
                        val deviceType = dataSnapshot.child("deviceType").getValue(String::class.java)
                        deviceType?.let {
                            trySend(deviceType)
                        }
                    }
                    close()
                }

                override fun onCancelled(error: DatabaseError) {
                    // You can handle the error here as you see fit
                }
            }
            query.addValueEventListener(listener)

            // Removing the listener when the flow is no longer collected
            awaitClose {
                query.removeEventListener(listener)
            }
        }
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

    override suspend fun getLoggedInDevices(): Flow<Response<Pair<List<DeviceData>?, Boolean?>>> =
        callbackFlow {
            auth.currentUser?.let {
                var _deviceList = mutableListOf<DeviceData>()
                val reference = db.reference.child("users").child(it.uid).child("userDevicesList")

                val listener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            for (ds1 in ds.children) {
                                val item = ds1.getValue(DeviceData::class.java)
                                item?.let {
                                    _deviceList.add(it)
                                }
                            }
                        }
                        trySend(Response.Success(_deviceList))
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

    private fun checkDeviceType(deviceId: String): Flow<DeviceType> = callbackFlow {
        auth.currentUser?.let {
            val reference =
                db.reference.child("users").child(it.uid).child("userDevicesList").child(deviceId)
                    .child("deviceType")

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                    }
                    DeviceType.Primary
//                    val item = dataSnapshot.getValue(String::class.java)
//                    println("item: $item")
//                    item?.let {
//                        println("item: $it")
//                        if (it == DeviceType.Primary.toString()) {
//                            DeviceType.Secondary
//                        } else {
//                            DeviceType.Primary
//                        }
//                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            reference.addValueEventListener(listener)
        }
        awaitClose { close() }
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
                _user?.let {
//                    addLocalSecretKeyInKeyStore(AES_ALIES_NAME, it.aesKey!!)
                    addUserInSharedPreferenceDB(it.aesKey, it.aesIV, it.publicUID!!)
                    Response.Success(
                        _user, true
                    ) // Return success response with the user object
                } ?: Response.Failure(Exception("User not found")) // Return failure response
            } catch (e: Exception) {
                Response.Failure(e) // Return failure response
            }
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
    }


    private fun addUserInSharedPreferenceDB(aesKey: String?, aesIV: String?, publicUID: String) {
        myPreference.aesCloudKey = aesKey!!
        myPreference.aesCloudIv = aesIV!!
        myPreference.publicUid = publicUID
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
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Response.Failure(error.toException())
                            _user = null
                        }
                    }
                    reference.addListenerForSingleValueEvent(listener)
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

        return if (email?.substringBeforeLast("@")?.contains(".") != true){
            email?.substringBeforeLast("@")
        }else{
            email.substringBeforeLast("@").replace(".", "_")
        }
    }


}
