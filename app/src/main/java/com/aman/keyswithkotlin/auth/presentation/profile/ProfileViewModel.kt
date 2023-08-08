package com.aman.keyswithkotlin.auth.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    val displayName get() = authUseCases.displayName
    val photoUrl get() = authUseCases.photoUrl

    var signOutResponse by mutableStateOf<SignOutResponse>(Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Success(false))
        private set


    fun signOut() = viewModelScope.launch {
        signOutResponse = Loading
        authUseCases.signOut().collect{response->
            signOutResponse = response
        }

    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Loading
        authUseCases.revokeAccess().collect{response->
            revokeAccessResponse = response
        }
    }
}