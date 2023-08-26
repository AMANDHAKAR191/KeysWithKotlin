package com.aman.keyswithkotlin.presentation

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.service.autofill.Dataset
import android.service.autofill.FillResponse
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.autofill_service.AutofillPasswordViewModel
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.ui.theme.KeysWithKotlinTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList


@AndroidEntryPoint
class BiometricAuthActivity : ComponentActivity() {


    companion object {
        const val EXTRA_DATASET = "dataset"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
        const val EXTRA_AUTOFILL_IDS = "autofillIds"
    }

    var usernameID: AutofillId? = null
    var passwordID: AutofillId? = null
    private var passwordList1 = listOf<Password>()
    private var autofillIds:ArrayList<AutofillId>? = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeysWithKotlinTheme {
                val viewModel: AutofillPasswordViewModel = hiltViewModel()
                passwordList1 = viewModel.state.value.passwords

                println("passwordList11: $passwordList1")
                if (passwordList1.isEmpty()){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }else{
                    launchBiometric()
                }
            }
        }
    }


    private fun onYes() {
        //            autofillIds = intent.getParcelableArrayListExtra(EXTRA_AUTOFILL_IDS, AutofillId::class.java)


        val fillResponse = FillResponse.Builder()
        passwordList1.forEach { password ->
            val datasetBuilder = Dataset.Builder()

            //create presentation for the dataset
            val presentation =
                RemoteViews(
                    applicationContext.packageName,
                    com.aman.keyswithkotlin.R.layout.autofill_list_item
                )
            presentation.setTextViewText(com.aman.keyswithkotlin.R.id.text, password.userName)

            //get autofillids from intent
            autofillIds = intent.getParcelableArrayListExtra(EXTRA_AUTOFILL_IDS)
            autofillIds?.let {
                println("tempUsernameID: ${it.get(0)} || tempPasswordID: ${it.get(1)}")

                //set the value to the fields
                println("usernameID: ${it.get(0)} ")
                if (it.get(0) != null){
                    datasetBuilder.setValue(it.get(0), AutofillValue.forText(password.userName), presentation)
                }

                println("passwordID: ${it.get(1)} ")
                if (it.get(1) != null){
                    datasetBuilder.setValue(it.get(1), AutofillValue.forText(password.password), presentation)
                }
            }

            println("dataset: ${datasetBuilder}")
            fillResponse.addDataset(datasetBuilder.build())
        }


        val replyIntent = Intent()
        // Send the data back to the service
        replyIntent.putExtra(EXTRA_AUTHENTICATION_RESULT, fillResponse.build())

        setResult(RESULT_OK, replyIntent)
        finish()
    }

    private fun onNo() {
        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_AUTHENTICATION_RESULT, false)
        setResult(RESULT_CANCELED, replyIntent)
        finish()
    }

    private fun launchBiometric() {
        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt.Builder(applicationContext)
                .setTitle("Keys want to verify your Biometric")
                .setDescription("Your Biometric is used to make secure authentication process.")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
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
                    onYes()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onNo()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onNo()
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
            notifyUser("Auth Cancelled via Signal")
        }

        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String) {
        Log.d("BIOMETRIC", message)
    }
}

fun AutofillId.toStringValue(): String {
    return this.toString()
}


// Store the mapping between your custom string IDs and AutofillIds
private val autofillIdMap = mutableMapOf<String, AutofillId>()

// Function to convert a custom string ID to an AutofillId
fun String.toAutofillId(): AutofillId? {
    return autofillIdMap[this]
}

// Function to register an AutofillId with a custom string ID
fun registerAutofillId(customId: String, autofillId: AutofillId) {
    autofillIdMap[customId] = autofillId
}

