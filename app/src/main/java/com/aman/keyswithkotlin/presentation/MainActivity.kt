package com.aman.keyswithkotlin.presentation

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global
import android.view.WindowManager
import android.view.autofill.AutofillManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aman.keyswithkotlin.auth.presentation.auth.AuthViewModel
import com.aman.keyswithkotlin.core.AppLockCounterClass
import com.aman.keyswithkotlin.core.BiometricAuthentication
import com.aman.keyswithkotlin.core.MyPreference
import com.aman.keyswithkotlin.navigation.Graph
import com.aman.keyswithkotlin.navigation.RootNavGraph
import com.aman.keyswithkotlin.ui.theme.KeysTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


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
//    var useDataTransferIsAllowed = false
    var isUsbDebuggingEnabled = false
    var isWireLessDebuggingEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContent {
            KeysTheme {
                Firebase.database.setPersistenceEnabled(true)
                mAutofillManager = getSystemService(AutofillManager::class.java) as AutofillManager

                FirebaseMessaging.getInstance().subscribeToTopic("UserName")

                navController = rememberNavController()

                // In your main activity, register the BroadcastReceiver with an IntentFilter
                val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                val receiver = ChargerCableReceiver(updateState = {
//                    println("useDataTransferIsAllowed: $useDataTransferIsAllowed")
//                    useDataTransferIsAllowed = it
                })
                registerReceiver(receiver, filter)

                isUsbDebuggingEnabled = isUsbDebuggingEnabled(this)
                isWireLessDebuggingEnabled = isWirelessDebuggingEnabled()

                // notify if USB debugging or Wireless Debugging is enable or not
//                LaunchedEffect(
//                    key1 = isUsbDebuggingEnabled,
//                    key2 = isWireLessDebuggingEnabled
//                ){
//                    println("isUsbDebuggingEnabled(this): $isUsbDebuggingEnabled")
////                    if (!useDataTransferIsAllowed) {
////                        // Dismiss the dialog automatically when useDataTransferIsAllowed is false
////                        return@LaunchedEffect
////                    }
//                    // USB debugging is enabled, prompt the user to turn it off
//
//                if (isUsbDebuggingEnabled || isWireLessDebuggingEnabled){
//                    println("isUsbDebuggingEnabled(this): $isUsbDebuggingEnabled")
//
//                    val builder = AlertDialog.Builder(this@MainActivity)
//                    builder
//                        .setCancelable(!(isUsbDebuggingEnabled || isWireLessDebuggingEnabled))
//                        .setTitle("Security Alert")
//                        .setMessage("USB debugging or Wireless debugging is enabled. Turn off USB debugging or Wireless debugging to use this app.")
//                        .setPositiveButton("Open Settings") { _, _ ->
//                            openDeveloperOptionsSettings(this@MainActivity)
//                        }
//                        .setNegativeButton("Cancel") { _, _ ->
//                            finish()
//                        }
//                    builder.create().show()
//                }
//                }


//                if (isUsbDebuggingEnabled(this) || isWirelessDebuggingEnabled()){
//                    val builder = AlertDialog.Builder(this@MainActivity)
//                    builder
//                        .setCancelable(!isUsbDebuggingEnabled(this) || !isWirelessDebuggingEnabled())
//                        .setTitle("Security Alert")
//                        .setMessage("USB debugging or Wireless debugging is enabled. Turn USB debugging or Wireless debugging off to use this app.")
//                        .setPositiveButton("Open Settings") { _, _ ->
//                            openDeveloperOptionsSettings(this@MainActivity)
//                        }
//                        .setNegativeButton("Cancel") { _, _ ->
//                            finish()
//                        }
//                    builder.create().show()
//                }
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

    // Check if USB debugging is enabled
    private fun isUsbDebuggingEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(this.contentResolver, Global.ADB_ENABLED, 0) === 1
    }

    // Check if wireless debugging is enabled
    private fun isWirelessDebuggingEnabled(): Boolean {
        // "this" is your activity or context
        return Settings.Global.getInt(this.contentResolver, "adb_wifi_enabled", 0) != 0
    }

    // Open the Developer Options settings to disable USB debugging
    fun openDeveloperOptionsSettings(context: Context) {
        val intent: Intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        context.startActivity(intent)
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
        if (isUsbDebuggingEnabled || isWireLessDebuggingEnabled){
            appLockCounter.onPauseOperation()
        }
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