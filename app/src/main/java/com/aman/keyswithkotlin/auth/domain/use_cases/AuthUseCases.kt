package com.aman.keyswithkotlin.auth.domain.use_cases

import com.aman.keyswithkotlin.access_verification.domain.use_cases.CheckAuthorizationOfDevice

data class AuthUseCases(
    val displayName: DisplayName,
    val photoUrl: PhotoUrl,
    val isUserAuthenticated: IsUserAuthenticated,
    val oneTapSignInWithGoogle: OneTapSignInWithGoogle,
    val firebaseSignInWithGoogle: FirebaseSignInWithGoogle,
    val signOut: SignOut,
    val revokeAccess: RevokeAccess,
    val getLoggedInDevices: GetLoggedInDevices
)
