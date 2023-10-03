package com.aman.keyswithkotlin.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.WindowManager
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aman.keyswithkotlin.auth.presentation.auth.AuthViewModel
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.navigation.Graph
import com.aman.keyswithkotlin.navigation.RootNavGraph
import com.aman.keyswithkotlin.notification_service.FCMNotificationSender
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<AuthViewModel>()
    private var mAutofillManager: AutofillManager? = null

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            Firebase.database.setPersistenceEnabled(true)
            mAutofillManager = getSystemService(AutofillManager::class.java) as AutofillManager

            FirebaseMessaging.getInstance().subscribeToTopic("UserName")

            navController = rememberNavController()
            RootNavGraph(
                navController = navController,
                mAutofillManager!!,
                this@MainActivity,
                this@MainActivity
            )
            val myPreference = MyPreference()
            println("myPreference.isNewUser: ${myPreference.isOldUser}")
            if (myPreference.isOldUser){
                println("check1")
                checkAuthState()
            }else{
                println("check2")
                navigateToOnBoardingScreens()
            }
//            checkAuthState()
        }
    }


    override fun onRestart() {
        super.onRestart()
        val notificationSender = FCMNotificationSender(
            "/topics/" + "UserName",
            "FCMessage test",
            "this is a test notification, Please ignore this notification.\nthis is a test notification, Please ignore this notification",
            this@MainActivity,
            this@MainActivity
        )
        notificationSender.sendNotification()
        Toast.makeText(this@MainActivity, "Notification Sent", Toast.LENGTH_SHORT).show()
//        launchBiometric()
    }

    private fun checkAuthState() {
        if (viewModel.isUserAuthenticated) {
//            launchBiometric()
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

    private fun launchBiometric() {
        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt.Builder(applicationContext)
                .setTitle("Keys want to verify your Biometric")
                .setDescription("Your Biometric is used to make secure authentication process.")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(
                getCancelletionSignal(),
                mainExecutor,
                authenticationCallBack
            )

        }
    }

    private val authenticationCallBack: BiometricPrompt.AuthenticationCallback
        get() =
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navigateToProfileScreen()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }
            }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isDeviceSecure) {
            notifyUser("lock screen security not enabled in the setting")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Finger print authentication permission not enabled")
            return false
        }
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }


    private fun getCancelletionSignal(): CancellationSignal {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            notifyUser("Ath Cancelled via Signal")
        }

        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String) {
        Log.d("BIOMETRIC", message)
    }

}