package com.aman.keyswithkotlin.auth.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.auth.domain.repository.OneTapSignInResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.auth.presentation.AuthEvent
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    val oneTapClient: SignInClient
) : ViewModel() {
    val isUserAuthenticated get() = authUseCases.isUserAuthenticated.invoke()
    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(Success(status = false))
        private set


    fun oneTapSignIn() = viewModelScope.launch {
        oneTapSignInResponse = Loading
        authUseCases.oneTapSignInWithGoogle().collect { response ->
            oneTapSignInResponse = response
        }
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        signInWithGoogleResponse = Loading
        authUseCases.firebaseSignInWithGoogle(googleCredential).collect { response ->
            when(response){
                is Response.Success ->{
                }

                else -> {}
            }
            signInWithGoogleResponse = response
        }
    }
}