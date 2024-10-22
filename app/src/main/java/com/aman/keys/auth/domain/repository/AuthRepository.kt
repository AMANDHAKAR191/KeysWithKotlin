package com.aman.keys.auth.domain.repository

import com.aman.keys.auth.domain.model.DeviceData
import com.aman.keys.auth.domain.model.User
import com.aman.keys.core.util.Response
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
    val email:String
    val photoUrl: String

    suspend fun signOut(): Flow<SignOutResponse>

    suspend fun revokeAccess(): Flow<RevokeAccessResponse>

    suspend fun getLoggedInDevices():Flow<Response<Pair<List<DeviceData>?, Boolean?>>>
}