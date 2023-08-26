package com.aman.keyswithkotlin.autofill_service

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.app.slice.Slice
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.InlinePresentation
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.util.ArrayMap
import android.util.Log
import android.util.Size
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.inline.InlinePresentationSpec
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.presentation.BiometricAuthActivity
import com.aman.keyswithkotlin.presentation.BiometricAuthActivity.Companion.EXTRA_AUTOFILL_IDS
import com.aman.keyswithkotlin.presentation.BiometricAuthActivity.Companion.EXTRA_DATASET
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Locale


class KeysAutofillService : AutofillService() {

//    @Inject
//    lateinit var passwordUseCases: PasswordUseCases
    override fun onConnected() {
        super.onConnected()
    }

    var hasDataSet:Boolean = false

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
        intent1.putExtra(EXTRA_AUTOFILL_IDS, arrayListOf(userNameId, passwordId))
        // Create a PendingIntent from the intent
        val pendingIntent1 = PendingIntent.getActivity(
            applicationContext, 0, intent1,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val intentSender1 = pendingIntent1.intentSender

        val responseBuilder = FillResponse.Builder()
        if (userNameId == null){
            responseBuilder.setAuthentication(arrayOf(passwordId), intentSender1, presentation)
        } else if (passwordId == null){
            responseBuilder.setAuthentication(arrayOf(userNameId), intentSender1, presentation)
        }else{
            responseBuilder.setAuthentication(arrayOf(userNameId, passwordId), intentSender1, presentation)
        }
        callback.onSuccess(responseBuilder.build())
    }


    private fun traverseViewStructure(node: ViewNode, action: (ViewNode) -> Unit) {
        action(node)
        for (i in 0 until node.childCount) {
            traverseViewStructure(node.getChildAt(i), action)
        }
    }

//    private fun getPasswords() {
//
//        scope.launch(Dispatchers.IO) {
//            passwordUseCases.getPasswords().collect { response ->
//                withContext(Dispatchers.Main) {
//                    when (response) {
//                        is Response.Success<*, *> -> {
//                            passwordList1 = response.data as MutableList<Password>
//                            println("passwordList: $passwordList")
//                        }
//
//                        is Response.Failure -> {
//
//                        }
//
//                        is Response.Loading -> {
//
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun fetchAutofillData(
        packageName: String,
        fields: MutableMap.MutableEntry<String?, AutofillId?>
    ): Dataset {
//        getPasswords() // Fetch the passwords
        val datasetBuilder = Dataset.Builder()
        println("yourAutofillId: $fields")
        passwordList.forEach { password ->
            val presentation = newDatasetPresentation(
                packageName,
                password.userName
            ) // how to create layout on compose
            datasetBuilder.setValue(
                fields.value!!, // Provide the proper AutofillId
                AutofillValue.forText(password.userName),
                presentation
            ).setValue(
                fields.value!!, // Provide the proper AutofillId
                AutofillValue.forText(password.password),
                presentation
            )
        }
        return datasetBuilder.build()
    }

    fun newDatasetPresentation(
        packageName: String,
        text: CharSequence
    ): RemoteViews {
        val presentation =
            RemoteViews("com.aman.keyswithkotlin", R.layout.autofill_list_item)
        presentation.setTextViewText(android.R.id.text1, text)
        presentation.setImageViewResource(android.R.id.icon, android.R.drawable.ic_delete)
        return presentation
    }

    private fun addAutofillableFields(
        fields: MutableMap<String?, AutofillId?>,
        node: ViewNode
    ) {
        val hints = node.autofillHints
        if (hints != null) {
            // We're simple, we only care about the first hint
            val hint = hints[0].lowercase(Locale.getDefault())
            val id = node.autofillId
            if (!fields.containsKey(hint)) {
                Log.v(TAG, "Setting hint '$hint' on $id")
                fields[hint] = id
            } else {
                Log.v(
                    TAG, "Ignoring hint '" + hint + "' on " + id
                            + " because it was already set"
                )
            }
        }
        val childrenSize = node.childCount
        for (i in 0 until childrenSize) {
            addAutofillableFields(fields, node.getChildAt(i))
        }
    }

    fun getLatestAssistStructure(request: FillRequest): AssistStructure {
        Log.d(TAG, "getFillContexts : " + request.fillContexts)
        val fillContexts = request.fillContexts
        Log.d(TAG, "getFillContexts : " + fillContexts[fillContexts.size - 1].requestId)
        Log.d(TAG, "getFillContexts : " + fillContexts[fillContexts.size - 1].describeContents())
        return fillContexts[fillContexts.size - 1].structure
    }


    private fun getAutofillableFields(structure: AssistStructure): MutableMap<String?, AutofillId?> {
        val fields: MutableMap<String?, AutofillId?> = ArrayMap()
        val nodes = structure.windowNodeCount
        for (i in 0 until nodes) {
            val node = structure.getWindowNodeAt(i).rootViewNode
            addAutofillableFields(fields, node)
        }
        return fields
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        val structure = request.fillContexts[request.fillContexts.size - 1].structure
        val packageName = structure.activityComponent.packageName
        println("$TAG packageName: $packageName")
//        if (!PackageVerifier.isValidPackage(applicationContext, packageName)) {
//            callback.onFailure("Invalid Package name")
//            return
//        }

        val clientState = request.clientState

        // Assuming you have a function to parse and save the data
        val success = saveAutofillData(clientState)

        if (success) {
            callback.onSuccess()
        } else {
            callback.onFailure("Failed to save data")
        }
    }


    private fun saveAutofillData(clientState: Bundle?): Boolean {
        // Parse the data from clientState and save it as per your app's logic
        // Return true if the data was saved successfully, false otherwise
        return true // Return the appropriate value based on your implementation
    }

}

// and my password data class
//data class Password constructor(
//    var userName: String = "",
//    var password: String = "",
//    val websiteName: String = "",
//    val websiteLink: String = "",
//    val timestamp: String = "",
//    val lastUsedTimeStamp: String = ""
//)