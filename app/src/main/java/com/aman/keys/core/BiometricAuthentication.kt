package com.aman.keys.core

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BiometricAuthentication(
    private val activity: Activity,
    private val context: Context,
) {

    fun launchBiometric(): Flow<BiometricStatus> = callbackFlow{

        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt.Builder(activity.applicationContext)
                .setTitle("Keys want to verify your Biometric")
                .setDescription("Your Biometric is used to make secure authentication process.")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(
                getCancelletionSignal(),
                activity.mainExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        trySend(BiometricStatus.SUCCEEDED)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        trySend(BiometricStatus.FAILED)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        trySend(BiometricStatus.ERROR)
                    }
                }
            )
        }
        awaitClose{
            close()
        }
    }


    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isDeviceSecure) {
//            notifyUser("lock screen security not enabled in the setting")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            notifyUser("Finger print authentication permission not enabled")
            return false
        }
        return activity.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }


    private fun getCancelletionSignal(): CancellationSignal {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
//            notifyUser("Auth Cancelled via Signal")
        }

        return cancellationSignal as CancellationSignal
    }
}

enum class BiometricStatus{
    SUCCEEDED, FAILED, ERROR
}