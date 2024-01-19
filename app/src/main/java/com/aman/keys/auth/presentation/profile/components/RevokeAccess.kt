package com.aman.keys.auth.presentation.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.aman.keys.auth.domain.repository.RevokeAccessResponse
import com.aman.keys.core.util.Response.Failure
import com.aman.keys.core.util.Response.Loading
import com.aman.keys.core.util.Response.Success
import com.aman.keys.presentation.CustomCircularProgressBar

@Composable
fun RevokeAccess(
    revokeAccessResponse: RevokeAccessResponse,
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
            showSnackBar()
        }
    }
}