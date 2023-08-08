package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.auth.domain.repository.AuthRepository
import com.aman.keyswithkotlin.auth.domain.repository.OneTapSignInResponse
import com.aman.keyswithkotlin.core.util.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OneTapSignInWithGoogle @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Response<Pair<BeginSignInResult?, Boolean?>>> {
        return authRepository.oneTapSignInWithGoogle()
    }
}