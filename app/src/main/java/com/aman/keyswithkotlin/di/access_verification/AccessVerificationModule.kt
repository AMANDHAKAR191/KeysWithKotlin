package com.aman.keyswithkotlin.di.access_verification

import com.aman.keyswithkotlin.access_verification.data.repository.AccessVerificationRepositoryImpl
import com.aman.keyswithkotlin.access_verification.domain.repository.AccessVerificationRepository
import com.aman.keyswithkotlin.access_verification.domain.use_cases.AccessVerificationUseCases
import com.aman.keyswithkotlin.access_verification.domain.use_cases.CancelAuthorizationAccessProcess
import com.aman.keyswithkotlin.access_verification.domain.use_cases.CheckAuthorizationOfDevice
import com.aman.keyswithkotlin.access_verification.domain.use_cases.CompleteAuthorizationAccessProcess
import com.aman.keyswithkotlin.access_verification.domain.use_cases.GetAccessRequesterClient
import com.aman.keyswithkotlin.access_verification.domain.use_cases.GiveAuthorizationAccessOfSecondaryDevice
import com.aman.keyswithkotlin.access_verification.domain.use_cases.RemoveAuthorizationAccessOfSecondaryDevice
import com.aman.keyswithkotlin.access_verification.domain.use_cases.RequestAuthorizationAccess
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.di.UID
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AccessVerificationModule {
    @Provides
    fun provideAccessVerificationRepository(
        db: FirebaseDatabase,
        @UID
        UID: String,
    ): AccessVerificationRepository = AccessVerificationRepositoryImpl(
        db = db,
        UID = UID
    )

    @Provides
    fun provideAccessVerificationUseCases(
        accessVerificationRepository: AccessVerificationRepository,
        myPreference: MyPreference
    ): AccessVerificationUseCases = AccessVerificationUseCases(
        checkAuthorizationOfDevice = CheckAuthorizationOfDevice(accessVerificationRepository, myPreference),
        giveAuthorizationAccessOfSecondaryDevice = GiveAuthorizationAccessOfSecondaryDevice(accessVerificationRepository),
        removeAuthorizationAccessOfSecondaryDevice = RemoveAuthorizationAccessOfSecondaryDevice(accessVerificationRepository),
        getAccessRequesterClient = GetAccessRequesterClient(accessVerificationRepository),
        requestAuthorizationAccess = RequestAuthorizationAccess(accessVerificationRepository),
        completeAuthorizationAccessProcess = CompleteAuthorizationAccessProcess(accessVerificationRepository),
        cancelAuthorizationAccessProcess = CancelAuthorizationAccessProcess(accessVerificationRepository)
    )

}