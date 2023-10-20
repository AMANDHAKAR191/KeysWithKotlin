package com.aman.keyswithkotlin.autofill_service

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.presentation.componants.SearchedPasswordItem
import com.aman.keyswithkotlin.ui.theme.KeysWithKotlinTheme
import dagger.hilt.android.AndroidEntryPoint
import java.lang.invoke.MethodType


@AndroidEntryPoint
class BiometricAuthActivity : ComponentActivity() {


    companion object {
        const val EXTRA_DATASET = "dataset"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
        const val EXTRA_CLIENT_PACKAGE_NAME = "clientPackageName"
        const val EXTRA_AUTOFILL_IDS = "autofillIds"
    }

    var usernameID: AutofillId? = null
    var passwordID: AutofillId? = null
    private var autofillIds: ArrayList<AutofillId>? = arrayListOf()
    private var passwordList = listOf<Password>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeysWithKotlinTheme {
                val viewModel: AutofillPasswordViewModel = hiltViewModel()
                passwordList = viewModel.state.value.passwords

                println("passwordList11: $passwordList")
                if (passwordList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
//                    val someWebsiteName = "20BCR70782"
                    val someWebsiteName = intent.getStringExtra(EXTRA_CLIENT_PACKAGE_NAME)
                    println("someWebsiteName: $someWebsiteName")
                    //write code filter passwordList based on given value websiteName and store in filteredPasswordList
                    var filteredPasswordList:List<Password> = passwordList.filter { it.websiteName == someWebsiteName }
                    if (filteredPasswordList.isEmpty()){
                        ShowPasswordList(passwordList)
                    }else{
                        ShowPasswordList(filteredPasswordList)
                    }
                    launchBiometric()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowPasswordList(passwordList1: List<Password>) {
        AlertDialog(
            modifier = Modifier.size(height = 500.dp, width = 300.dp),
            onDismissRequest = { /*TODO*/ }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LazyColumn {
                    items(passwordList1) {
                        SearchedPasswordItem(
                            password = it,
                            onItemClick = { onYes(listOf(it)) }
                        )
                    }
                }
            }

        }
    }


    private fun onYes(tempPasswordList:List<Password>) {
        val fillResponse = FillResponse.Builder()
        tempPasswordList.forEach { password ->
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
                if (it.get(0) != null) {
                    datasetBuilder.setValue(
                        it.get(0),
                        AutofillValue.forText(password.userName),
                        presentation
                    )
                }

                println("passwordID: ${it.get(1)} ")
                if (it.get(1) != null) {
                    datasetBuilder.setValue(
                        it.get(1),
                        AutofillValue.forText(password.password)
                    )
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
                .setTitle("Keys want to verify you")
                .setDescription("Your Biometric is used to make authentication process more secure.")
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
//                    onYes()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
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



