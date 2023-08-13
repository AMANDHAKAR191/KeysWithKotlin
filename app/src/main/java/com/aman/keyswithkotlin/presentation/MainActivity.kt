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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aman.keyswithkotlin.auth.presentation.auth.AuthViewModel
import com.aman.keyswithkotlin.core.DeviceInfo
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.navigation.Graph
import com.aman.keyswithkotlin.navigation.RootNavGraph
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<AuthViewModel>()


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            val scope = rememberCoroutineScope()
            Firebase.database.setPersistenceEnabled(true)
            val context = LocalContext.current
            val deviceInfo = DeviceInfo(context)

            println("deviceInfo.getDeviceId(): ${deviceInfo.getDeviceId()}")
            println("deviceInfo.getAppVersion(): ${deviceInfo.getAppVersion()}")
            println("deviceInfo.getLastLoginTimeStamp(): ${deviceInfo.getLastLoginTimeStamp()}")


            navController = rememberNavController()
            RootNavGraph(
                navController = navController,
                lifecycleOwner = this
            )
            checkAuthState()
        }
    }

    override fun onRestart() {
        super.onRestart()
//        launchBiometric()
    }

    private fun checkAuthState() {
        if (viewModel.isUserAuthenticated) {
            launchBiometric()
        }
    }

    private fun navigateToProfileScreen() {
        navController.popBackStack()
        navController.navigate(Graph.ACCESS_VERIFICATION)
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