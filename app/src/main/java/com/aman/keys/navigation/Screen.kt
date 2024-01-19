package com.aman.keys.navigation


sealed class Screen(val route: String) {
    object OnBoardingScreen : Screen(ON_BOARDING_SCREEN)
    object AuthScreen : Screen(AUTH_SCREEN)
    object ProfileScreen : Screen(PROFILE_SCREEN)

    object AccessVerificationScreen:Screen(ACCESS_VERIFICATION_SCREEN)

    object AddEditPasswordScreen : Screen(ADD_EDIT_PASSWORD_SCREEN)
    object GeneratePasswordScreen : Screen(GENERATE_PASSWORD_SCREEN)
    object RecentGeneratedPasswordScreen : Screen(RECENT_GENERATE_PASSWORD_SCREEN)

    object AddEditNoteScreen : Screen(ADD_EDIT_NOTE_SCREEN)

    object IndividualChatScreen : Screen(INDIVIDUAL_CHAT_SCREEN)
    object AppInfoScreen : Screen(APP_INFO_SCREEN)
    object ManageDevicesScreen : Screen(APP_INFO_SCREEN)
}

//Screens
const val ON_BOARDING_SCREEN = "OnBoardingScreen"
const val AUTH_SCREEN = "AuthenticationScreen"
const val PROFILE_SCREEN = "ProfileScreen"
const val ACCESS_VERIFICATION_SCREEN = "AccessVerificationScreen"
const val PASSWORD_SCREEN = "PasswordScreen"
const val ADD_EDIT_PASSWORD_SCREEN = "AddEditPasswordScreen"
const val GENERATE_PASSWORD_SCREEN = "GeneratePasswordScreen"
const val RECENT_GENERATE_PASSWORD_SCREEN = "RecentGeneratedPasswordScreen"
const val ADD_EDIT_NOTE_SCREEN = "AddEditNoteScreen"
const val INDIVIDUAL_CHAT_SCREEN = "IndividualChatScreen"
const val APP_INFO_SCREEN = "AppInfoScreen"
const val MANAGE_DEVICES_SCREEN = "ManageDevicesScreen"