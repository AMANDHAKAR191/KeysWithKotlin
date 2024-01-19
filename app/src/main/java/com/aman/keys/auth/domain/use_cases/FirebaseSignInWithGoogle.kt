package com.aman.keys.auth.domain.use_cases

import com.aman.keys.auth.domain.repository.AuthRepository
import com.aman.keys.auth.domain.repository.SignInWithGoogleResponse
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