package com.aman.keyswithkotlin.access_verification.domain.use_cases

data class AccessVerificationUseCases (
    val checkAuthorizationOfDevice: CheckAuthorizationOfDevice,
    val giveAuthorizationAccessOfSecondaryDevice: GiveAuthorizationAccessOfSecondaryDevice,
    val removeAuthorizationAccessOfSecondaryDevice: RemoveAuthorizationAccessOfSecondaryDevice,
    val getAccessRequesterClient: GetAccessRequesterClient,
    val requestAuthorizationAccess: RequestAuthorizationAccess,
    val completeAuthorizationAccessProcess: CompleteAuthorizationAccessProcess,
    val cancelAuthorizationAccessProcess: CancelAuthorizationAccessProcess
)