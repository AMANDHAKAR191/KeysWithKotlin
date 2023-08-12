package com.aman.keyswithkotlin.navigation


sealed class Screen(val route: String) {
    object AuthScreen : Screen(AUTH_SCREEN)
    object ProfileScreen : Screen(PROFILE_SCREEN)

    object AccessVerificationScreen:Screen(ACCESS_VERIFICATION_SCREEN)

    object AddEditPasswordScreen : Screen(ADD_EDIT_PASSWORD_SCREEN)
    object GeneratePasswordScreen : Screen(GENERATE_PASSWORD_SCREEN)
    object RecentGeneratedPasswordScreen : Screen(RECENT_GENERATE_PASSWORD_SCREEN)

    object AddEditNoteScreen : Screen(ADD_EDIT_NOTE_SCREEN)

    object IndividualChatScreen : Screen(INDIVIDUAL_CHAT_SCREEN)
}

//Screens
const val AUTH_SCREEN = "AuthenticationScreen"
const val PROFILE_SCREEN = "ProfileScreen"
const val ACCESS_VERIFICATION_SCREEN = "AccessVerificationScreen"
const val PASSWORD_SCREEN = "PasswordScreen"
const val ADD_EDIT_PASSWORD_SCREEN = "AddEditPasswordScreen"
const val GENERATE_PASSWORD_SCREEN = "GeneratePasswordScreen"
const val RECENT_GENERATE_PASSWORD_SCREEN = "RecentGeneratedPasswordScreen"
const val ADD_EDIT_NOTE_SCREEN = "AddEditNoteScreen"
const val INDIVIDUAL_CHAT_SCREEN = "IndividualChatScreen"