package com.aman.keyswithkotlin.auth.domain.repository

import com.aman.keyswithkotlin.auth.domain.model.User
import com.aman.keyswithkotlin.core.util.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

typealias OneTapSignInResponse = Response<Pair<BeginSignInResult?, Boolean?>>
typealias SignInWithGoogleResponse = Response<Pair<User?, Boolean?>>

typealias SignOutResponse = Response<Pair<Boolean?, Boolean?>>
typealias RevokeAccessResponse = Response<Pair<Boolean?, Boolean?>>


interface AuthRepository {
    val isUserAuthenticatedInFirebase: Boolean

    suspend fun oneTapSignInWithGoogle(): Flow<OneTapSignInResponse>

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): Flow<SignInWithGoogleResponse>

    val displayName: String
    val photoUrl: String

    suspend fun signOut(): Flow<SignOutResponse>

    suspend fun revokeAccess(): Flow<RevokeAccessResponse>

    fun checkAuthorizationOfDevice(deviceId: String): Flow<Response<Pair<String?, Boolean?>>>
}