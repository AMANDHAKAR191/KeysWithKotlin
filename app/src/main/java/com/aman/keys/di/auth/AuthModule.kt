package com.aman.keys.di.auth

import android.app.Application
import android.content.Context
import com.aman.keys.Keys
import com.aman.keys.auth.data.repository.AuthRepositoryImpl
import com.aman.keys.auth.domain.repository.AuthRepository
import com.aman.keys.auth.domain.use_cases.AuthUseCases
import com.aman.keys.auth.domain.use_cases.DisplayName
import com.aman.keys.auth.domain.use_cases.Email
import com.aman.keys.auth.domain.use_cases.FirebaseSignInWithGoogle
import com.aman.keys.auth.domain.use_cases.GetLoggedInDevices
import com.aman.keys.auth.domain.use_cases.IsUserAuthenticated
import com.aman.keys.auth.domain.use_cases.OneTapSignInWithGoogle
import com.aman.keys.auth.domain.use_cases.PhotoUrl
import com.aman.keys.auth.domain.use_cases.RevokeAccess
import com.aman.keys.auth.domain.use_cases.SignOut
import com.aman.keys.core.Constants
import com.aman.keys.core.MyPreference
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
class AuthModule {
    //for authentication
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Named(Constants.SIGN_IN_REQUEST)
    fun provideSignInRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(Keys.web_client_id)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
//        .setAutoSelectEnabled(true)
        .build()

    @Provides
    @Named(Constants.SIGN_UP_REQUEST)
    fun provideSignUpRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(Keys.web_client_id)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    @Provides
    fun provideGoogleSignInOptions(
        app: Application
    ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Keys.web_client_id)
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        googleSignInClient: GoogleSignInClient,
        @Named(Constants.SIGN_IN_REQUEST)
        signInRequest: BeginSignInRequest,
        @Named(Constants.SIGN_UP_REQUEST)
        signUpRequest: BeginSignInRequest,
        db: FirebaseDatabase,
        myPreference: MyPreference
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        googleSignInClient = googleSignInClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        db = db,
        myPreference = myPreference
    )


    @Provides
    fun provideAuthUseCases(
        authRepository: AuthRepository,
    ):AuthUseCases{
        return AuthUseCases(
            isUserAuthenticated = IsUserAuthenticated(authRepository),
            displayName = DisplayName(authRepository),
            email = Email(authRepository),
            photoUrl = PhotoUrl(authRepository),
            oneTapSignInWithGoogle = OneTapSignInWithGoogle(authRepository),
            firebaseSignInWithGoogle = FirebaseSignInWithGoogle(authRepository),
            signOut = SignOut(authRepository),
            revokeAccess = RevokeAccess(authRepository),
            getLoggedInDevices = GetLoggedInDevices(authRepository)
        )
    }
}

