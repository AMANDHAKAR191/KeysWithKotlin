package com.aman.keyswithkotlin.auth.presentation.profile.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.auth.domain.repository.RevokeAccessResponse
import com.aman.keyswithkotlin.core.util.Response.Failure
import com.aman.keyswithkotlin.core.util.Response.Loading
import com.aman.keyswithkotlin.core.util.Response.Success
import com.aman.keyswithkotlin.core.components.ProgressBar
import com.aman.keyswithkotlin.auth.presentation.profile.ProfileViewModel
import com.aman.keyswithkotlin.presentation.CustomCircularProgressBar

@Composable
fun RevokeAccess(
    revokeAccessResponse:RevokeAccessResponse,
    navigateToAuthScreen: (accessRevoked: Boolean) -> Unit,
    showSnackBar: () -> Unit
) {
    when (revokeAccessResponse) {
        is Loading -> CustomCircularProgressBar()
        is Success -> revokeAccessResponse.data?.let { accessRevoked ->
            LaunchedEffect(accessRevoked) {
                navigateToAuthScreen(accessRevoked)
            }
        }

        is Failure -> LaunchedEffect(Unit) {
            print(revokeAccessResponse.e)
            showSnackBar()
        }
    }
}