package com.aman.keyswithkotlin.autofill_service

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.aman.keyswithkotlin.autofill_service.ui.theme.KeysWithKotlinTheme
import com.aman.keyswithkotlin.passwords.domain.model.Password
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SaveAutofillPasswordActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PASSWORD_DATA = "passwordData"
    }

    var isSavePassword: Boolean = false
    var passwordData: ArrayList<String>? = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeysWithKotlinTheme {
                val viewModel: SaveAutofillPasswordViewModel = hiltViewModel()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    passwordData = intent.getStringArrayListExtra(EXTRA_PASSWORD_DATA)
                    println("passwordData: $passwordData")
                    passwordData?.let { passwordData ->
                        println("passwordData: check1: $passwordData")
                        viewModel.onEvent(
                            AutofillPasswordEvent.SavePassword(
                                Password(
                                    userName = passwordData.get(0),
                                    password = passwordData.get(1),
                                    websiteName =passwordData.get(2).replace('.','_'),
                                    websiteLink =passwordData.get(3)
                                )
                            )
                        )
                    }
                }
                LaunchedEffect(key1 = viewModel.state) {
                    viewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is AutofillPasswordEvent.PasswordNotSaved -> {
                                Toast.makeText(applicationContext, event.error, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            AutofillPasswordEvent.PasswordSaved -> {
                                Toast.makeText(applicationContext, "Saved", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK)
                                finish()
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
