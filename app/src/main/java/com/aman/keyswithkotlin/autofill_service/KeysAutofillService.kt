package com.aman.keyswithkotlin.autofill_service

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Bundle
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.util.ArrayMap
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.aman.keyswithkotlin.passwords.domain.use_cases.PasswordUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Locale
import javax.inject.Inject


class KeysAutofillService : AutofillService() {

    @Inject
    lateinit var passwordUseCases: PasswordUseCases

    private val TAG: String = "KeysAutofillService"
    val scope = CoroutineScope(Dispatchers.IO)
    private val passwordList = mutableListOf(
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

    //    var passwordList = mutableListOf<Password>()
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts[request.fillContexts.size - 1].structure
        val packageName = structure.activityComponent.packageName
        Log.d(TAG, " packageName: $packageName")
//        if (!PackageVerifier.isValidPackage(applicationContext, packageName)) {
//            callback.onFailure("Invalid Package name")
//            return
//        }
        val data = request.clientState
        Log.d(TAG, "onFillRequest(): data = $data")
        cancellationSignal.setOnCancelListener {
            Log.d(TAG, "Request cancelled")
        }
        // Find autofillable fields
        // Find autofillable fields
//        val structure: AssistStructure = getLatestAssistStructure(request)
        Log.d(TAG, "structure ${structure.activityComponent} +${structure.describeContents()}")
        val fields: MutableMap<String?, AutofillId?> = getAutofillableFields(structure)
        Log.d(TAG, "autofillable fields:$fields")
        var autofillData: Dataset? = null
        var response:FillResponse? = null
        fields.entries.forEach {
            println("it.key: ${it.key} == it.value: ${it.value}")
            // Assuming you have a function to fetch autofill data based on the package name or other criteria
            println("packageName: $packageName")
            autofillData = fetchAutofillData(packageName, it)
            response = FillResponse.Builder()
                .addDataset(autofillData)
                .build()

        }
        if (response != null) {
            callback.onSuccess(response)
        } else {
            callback.onFailure("No autofill data available")
        }


    }

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

    /**
     * Helper method to create a dataset presentation with the given text.
     */
    fun newDatasetPresentation(
        packageName: String,
        text: CharSequence
    ): RemoteViews {
        val presentation =
            RemoteViews("com.aman.keyswithkotlin", android.R.layout.activity_list_item)
        presentation.setTextViewText(android.R.id.text1, text)
        presentation.setImageViewResource(android.R.id.icon, android.R.drawable.ic_delete)
        return presentation
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

    fun getLatestAssistStructure(request: FillRequest): AssistStructure {
        Log.d(TAG, "getFillContexts : " + request.fillContexts)
        val fillContexts = request.fillContexts
        Log.d(TAG, "getFillContexts : " + fillContexts[fillContexts.size - 1].requestId)
        Log.d(TAG, "getFillContexts : " + fillContexts[fillContexts.size - 1].describeContents())
        return fillContexts[fillContexts.size - 1].structure
    }

    /**
     * Adds any autofillable view from the [ViewNode] and its descendants to the map.
     */
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


    //complete this code
//    private fun getPasswords() {
//        scope.launch(Dispatchers.IO) {
//            passwordUseCases.getPasswords().collect { response ->
//                withContext(Dispatchers.Main) {
//                    when (response) {
//                        is Response.Success<*, *> -> {
//                            passwordList = response.data as MutableList<Password>
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