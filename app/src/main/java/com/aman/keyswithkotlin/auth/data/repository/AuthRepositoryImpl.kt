package com.aman.keyswithkotlin.auth.data.repository

import com.aman.keyswithkotlin.auth.domain.model.User
import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.auth.domain.repository.OneTapSignInResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keyswithkotlin.core.AES
import com.aman.keyswithkotlin.core.Constants.SIGN_IN_REQUEST
import com.aman.keyswithkotlin.core.Constants.SIGN_UP_REQUEST
import com.aman.keyswithkotlin.core.Constants.USERS
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.di.AESKeySpecs
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Random
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseDatabase,
    private val aesKeySpecs: AESKeySpecs
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null
    private val _userData: User? = null
    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult ,true)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Response.Success(signUpResult, true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): SignInWithGoogleResponse {
        val statusDeferred = CompletableDeferred<SignInWithGoogleResponse>()

        try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

            println("isNewUser: $isNewUser")

            coroutineScope {
                val addUserDeferred = async { addUserToFireBase(auth.currentUser) }
                val getUserDeferred = async { getUserFromFireBase(auth.currentUser) }

                val status = if (isNewUser) {
                    println("new user")
                    addUserDeferred.await()
                } else {
                    println("old user")
                    getUserDeferred.await()
                }
                status.let {
                    statusDeferred.complete(it)
                }
            }
        } catch (e: Exception) {
            println("Error1: ${e.message}\n${e.printStackTrace()}")
            statusDeferred.complete(Response.Failure(e))
        }

        val status = statusDeferred.await()
        println("status: $status")
        return status
    }


    private suspend fun addUserToFireBase(
        user: FirebaseUser?
    ): SignInWithGoogleResponse = coroutineScope {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        val aesKey = "${generatePassword(22, specialCharacters = false)}=="
        val aesIV = generatePassword(22, specialCharacters = false)
        user?.let {
            val newUser = User(
                displayName = it.displayName,
                email = it.email,
                photoUrl = it.photoUrl?.toString(),
                aesKey = aesKey,
                aesIV = aesIV,
                privateUID = it.uid,
                publicUID = it.email,
                createdAt = formatted
            )

            AES.getInstance(aesKey, aesIV)?.let { aes ->
                val encryptedUser = encryptUser(newUser, aes)
                println("Adding user0 to Firebase...")
                try {
                    db.reference.child(USERS).child(it.uid)
                        .setValue(encryptedUser)
                        .await() // Wait for the Firebase operation to complete

                    Response.Success(status = true) // Return success response
                } catch (e: Exception) {
                    Response.Failure(e) // Return failure response
                }
            } ?: Response.Failure(Exception("AES initialization failed."))
        } ?: Response.Failure(Exception("Invalid FirebaseUser."))
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
                        Response.Success(_user, true) // Return success response with the user object
                    } else {
                        Response.Failure(Exception("User not found")) // Return failure response
                    }
                } catch (e: Exception) {
                    Response.Failure(e) // Return failure response
                }
            } ?: Response.Failure(Exception("Invalid FirebaseUser."))
        }
    }

    private suspend fun getUserFromFireBase(user: FirebaseUser?): SignInWithGoogleResponse {
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
                        Response.Success(_user, true) // Return success response with the user object
                    } ?: Response.Failure(Exception("User not found")) // Return failure response
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
