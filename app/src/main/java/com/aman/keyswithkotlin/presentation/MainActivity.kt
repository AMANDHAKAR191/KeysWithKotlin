package com.aman.keyswithkotlin.presentation

import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aman.keyswithkotlin.auth.presentation.auth.AuthViewModel
import com.aman.keyswithkotlin.core.AppLockCounterClass
import com.aman.keyswithkotlin.core.BiometricAuthentication
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.navigation.Graph
import com.aman.keyswithkotlin.navigation.RootNavGraph
import com.aman.keyswithkotlin.notification_service.FCMNotificationSender
import com.aman.keyswithkotlin.ui.theme.KeysTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var appLockCounter: AppLockCounterClass
    private val viewModel by viewModels<AuthViewModel>()
    private var mAutofillManager: AutofillManager? = null

    private var biometricAuthentication: BiometricAuthentication =
        BiometricAuthentication(
            this@MainActivity,
            this@MainActivity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            KeysTheme {
                Firebase.database.setPersistenceEnabled(true)
                mAutofillManager = getSystemService(AutofillManager::class.java) as AutofillManager

                FirebaseMessaging.getInstance().subscribeToTopic("UserName")

                navController = rememberNavController()
                RootNavGraph(
                    navController = navController,
                    mAutofillManager!!,
                    biometricAuthentication,
                    this@MainActivity,
                    this@MainActivity
                )
                val myPreference = MyPreference()
                println("myPreference.isNewUser: ${myPreference.isOldUser}")
                if (myPreference.isOldUser) {
                    checkAuthState()
                } else {
                    navigateToOnBoardingScreens()
                }

                appLockCounter = AppLockCounterClass(
                    myPreference,
                    this@MainActivity,
                    this@MainActivity,
                    finishActivity = {
                        println("finishActivity()")
                        finish()
                    })
                appLockCounter.initializeCounter()
                appLockCounter.onStartOperation()
            }
        }
    }


    override fun onRestart() {
        super.onRestart()
//        val notificationSender = FCMNotificationSender(
//            "/topics/" + "UserName",
//            "FCMessage test",
//            "this is a test notification, Please ignore this notification.\nthis is a test notification, Please ignore this notification",
//            this@MainActivity,
//            this@MainActivity
//        )
//        notificationSender.sendNotification()
//        Toast.makeText(this@MainActivity, "Notification Sent", Toast.LENGTH_SHORT).show()
        appLockCounter.onStartOperation()
    }

    override fun onPause() {
        super.onPause()
        appLockCounter.onPauseOperation()
    }

    private fun checkAuthState() {
        if (viewModel.isUserAuthenticated) {
            navigateToProfileScreen()
        }
    }

    private fun navigateToProfileScreen() {
        navController.popBackStack()
        navController.navigate(Graph.HOME)
    }

    private fun navigateToOnBoardingScreens() {
        navController.popBackStack()
        navController.navigate(Graph.ON_BOARDING)
    }

}