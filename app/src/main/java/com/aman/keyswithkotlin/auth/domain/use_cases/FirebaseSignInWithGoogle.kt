package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.auth.domain.model.User
import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keyswithkotlin.core.util.Response
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseSignInWithGoogle @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(googleAuthCredential: AuthCredential):Flow<SignInWithGoogleResponse>{
        return authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }
}