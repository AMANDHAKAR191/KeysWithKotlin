package com.aman.keys.auth.domain.use_cases

data class AuthUseCases(
    val displayName: DisplayName,
    val email: Email,
    val photoUrl: PhotoUrl,
    val isUserAuthenticated: IsUserAuthenticated,
    val oneTapSignInWithGoogle: OneTapSignInWithGoogle,
    val firebaseSignInWithGoogle: FirebaseSignInWithGoogle,
    val signOut: SignOut,
    val revokeAccess: RevokeAccess,
    val getLoggedInDevices: GetLoggedInDevices
)
