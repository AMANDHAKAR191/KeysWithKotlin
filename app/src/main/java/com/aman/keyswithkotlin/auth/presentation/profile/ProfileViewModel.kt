package com.aman.keyswithkotlin.auth.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aman.keyswithkotlin.auth.domain.model.DeviceData
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.auth.domain.repository.SignOutResponse
import com.aman.keyswithkotlin.auth.domain.use_cases.AuthUseCases
import com.aman.keyswithkotlin.core.util.Response
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import com.aman.keyswithkotlin.passwords.presentation.password_screen.PasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    val displayName get() = authUseCases.displayName
    val photoUrl get() = authUseCases.photoUrl

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    var signOutResponse by mutableStateOf<SignOutResponse>(Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Success(false))
        private set

    init {
        viewModelScope.launch {
            authUseCases.getLoggedInDevices().collect {response->
                when(response){
                    is Success->{
                        println("response.data: ${response.data as List<DeviceData>}")
                        _state.value = state.value.copy(
                            loggedInDeviceList = response.data as List<DeviceData>
                        )
                    }

                    is Response.Failure -> {

                    }
                    Loading -> {

                    }
                }

            }
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutResponse = Loading
        authUseCases.signOut().collect { response ->
            signOutResponse = response
        }

    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Loading
        authUseCases.revokeAccess().collect { response ->
            revokeAccessResponse = response
        }
    }
}