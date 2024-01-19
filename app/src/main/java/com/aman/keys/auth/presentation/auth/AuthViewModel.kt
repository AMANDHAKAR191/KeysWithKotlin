package com.aman.keys.auth.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keys.auth.domain.repository.OneTapSignInResponse
import com.aman.keys.auth.domain.repository.SignInWithGoogleResponse
import com.aman.keys.auth.domain.use_cases.AuthUseCases
import com.aman.keys.core.MyPreference
import com.aman.keys.core.util.Response
import com.aman.keys.core.util.Response.Loading
import com.aman.keys.core.util.Response.Success
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val myPreference: MyPreference,
    val oneTapClient: SignInClient
) : ViewModel() {


    val isUserAuthenticated get() = authUseCases.isUserAuthenticated.invoke()
    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(Success(status = false))
        private set


    fun oneTapSignInWithGoogle() = viewModelScope.launch {
        oneTapSignInResponse = Loading(message = null)
        authUseCases.oneTapSignInWithGoogle().collect { response ->
            oneTapSignInResponse = response
        }
    }

    fun signInWithFirebaseGoogleAccount(googleCredential: AuthCredential) = viewModelScope.launch {
        signInWithGoogleResponse = Loading(message = null)
        authUseCases.firebaseSignInWithGoogle(googleCredential).collect { response ->
            when(response) {
                is Response.Success -> {
                    myPreference.isOldUser = true
                }

                else -> {}
            }
            println("signInWithGoogleResponse: $response")
            signInWithGoogleResponse = response
        }
    }
}