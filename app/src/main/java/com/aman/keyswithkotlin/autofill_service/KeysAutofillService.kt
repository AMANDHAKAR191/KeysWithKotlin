package com.aman.keyswithkotlin.autofill_service

import android.app.PendingIntent
import android.app.assist.AssistStructure.ViewNode
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_AUTOFILL_IDS
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_CLIENT_PACKAGE_NAME
import com.aman.keyswithkotlin.autofill_service.SaveAutofillPasswordActivity.Companion.EXTRA_PASSWORD_DATA
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class KeysAutofillService : AutofillService() {

    //    @Inject
//    lateinit var passwordUseCases: PasswordUseCases
    override fun onConnected() {
        super.onConnected()
    }

    var hasDataSet: Boolean = false

    private val TAG: String = "KeysAutofillService"
    val scope = CoroutineScope(Dispatchers.IO)
    private var passwordList1 = mutableListOf<Password>()
    private var passwordList = mutableListOf(
        Password(
            "user1",
            "pass1",
            "example1.com",
            "https://www.example1.com",
            "timestamp1",
            "lastUsed1"
        ),
        Password(
            "user2",
            "pass2",
            "example2.com",
            "https://www.example2.com",
            "timestamp2",
            "lastUsed2"
        ),
        Password(
            "user3",
            "pass3",
            "example3.com",
            "https://www.example3.com",
            "timestamp3",
            "lastUsed3"
        )
    )

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {

        val fillContexts = request.fillContexts
        val structure = fillContexts.last().structure
        val packageName = structure.activityComponent.packageName
        println("$TAG onFillRequest")
        Log.d(TAG, " packageName: $packageName")
//        getPasswords()

        var userNameId: AutofillId? = null
        var passwordId: AutofillId? = null

        println("AUTOFILL_HINT_USERNAME: ${View.AUTOFILL_HINT_USERNAME}")
        println("AUTOFILL_HINT_PASSWORD: ${View.AUTOFILL_HINT_PASSWORD}")
        //traverse the structure
        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode
            traverseViewStructure(viewNode) { node ->
                if (node.autofillHints?.contains(View.AUTOFILL_HINT_USERNAME) == true) {
                    userNameId = node.autofillId
                    println("userNameId: $userNameId")
                }
                if (node.autofillHints?.contains(View.AUTOFILL_HINT_PASSWORD) == true) {
                    passwordId = node.autofillId
                    println("passwordId: $passwordId")
                }
            }
        }

        //create presentation for the dataset
        val presentation =
            RemoteViews(applicationContext.packageName, R.layout.your_auth_layout)

        val intent1 = Intent(applicationContext, BiometricAuthActivity::class.java)
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent1.putExtra(EXTRA_CLIENT_PACKAGE_NAME, packageName.replace('.', '_'))
        intent1.putExtra(EXTRA_AUTOFILL_IDS, arrayListOf(userNameId, passwordId))
        // Create a PendingIntent from the intent
        val pendingIntent1 = PendingIntent.getActivity(
            applicationContext, 0, intent1,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val intentSender1 = pendingIntent1.intentSender

        val responseBuilder = FillResponse.Builder()
        if (userNameId == null) {
            responseBuilder.setAuthentication(arrayOf(passwordId), intentSender1, presentation)
        } else if (passwordId == null) {
            responseBuilder.setAuthentication(arrayOf(userNameId), intentSender1, presentation)
        } else {
            responseBuilder.setAuthentication(
                arrayOf(userNameId, passwordId),
                intentSender1,
                presentation
            )
            responseBuilder
                .setSaveInfo(
                    SaveInfo.Builder(
                        SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                        arrayOf(userNameId, passwordId)
                    ).build()
                )
        }
        callback.onSuccess(responseBuilder.build())
    }


    private fun traverseViewStructure(node: ViewNode, action: (ViewNode) -> Unit) {
        action(node)
        for (i in 0 until node.childCount) {
            traverseViewStructure(node.getChildAt(i), action)
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val structure = request.fillContexts.last().structure
        val packageName = structure.activityComponent.packageName
        println("$TAG onSaveRequest")
        println("$TAG packageName: $packageName")

        var userName: String? = null
        var password: String? = null

        // Traverse the structure and find the AutofillValues
        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode
            traverseViewStructure(viewNode){node->
                if (node.autofillHints?.contains(View.AUTOFILL_HINT_USERNAME) == true) {
                    val value = node.autofillValue
                    if (value?.isText!! && value?.textValue != null) {
                        userName = value.textValue.toString()
                    }
                    println("userName: $userName")
                }
                if (node.autofillHints?.contains(View.AUTOFILL_HINT_PASSWORD) == true) {
                    val value = node.autofillValue
                    if (value?.isText!! && value?.textValue != null) {
                        password = value.textValue.toString()
                    }
                    println("password: $password")
                }
            }
        }

        if (password?.isNotEmpty() == true && userName?.isNotEmpty() == true){
            val intent1 = Intent(applicationContext, SaveAutofillPasswordActivity::class.java)
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent1.putExtra(EXTRA_PASSWORD_DATA, arrayListOf(userName, password, packageName, ""))
                applicationContext.startActivity(intent1)
        }

        // TODO: Here you would save the username and password.
        // Maybe call an API or store it in a secure place like Android Keystore.

        // Notify the system that the save operation was a success
        callback.onSuccess()
    }

}
