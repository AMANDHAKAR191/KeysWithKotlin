package com.aman.keyswithkotlin.auth.domain.repository

import com.aman.keyswithkotlin.auth.domain.model.User
import com.aman.keyswithkotlin.core.util.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential

typealias OneTapSignInResponse = Response<Pair<BeginSignInResult?,Boolean?>>
typealias SignInWithGoogleResponse = Response<Pair<User?,Boolean?>>


interface AuthRepository {
    val isUserAuthenticatedInFirebase: Boolean

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse
}