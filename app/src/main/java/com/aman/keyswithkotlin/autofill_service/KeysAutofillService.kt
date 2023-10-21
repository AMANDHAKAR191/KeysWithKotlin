package com.aman.keyswithkotlin.autofill_service

import android.app.PendingIntent
import android.app.assist.AssistStructure
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
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import android.widget.Toast
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_AUTOFILL_IDS
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_CLIENT_PACKAGE_NAME
import com.aman.keyswithkotlin.autofill_service.SaveAutofillPasswordActivity.Companion.EXTRA_PASSWORD_DATA
import java.util.Arrays


class KeysAutofillService : AutofillService() {

    private final val LINKEDIN_LOGIN_EMAIL_INPUT_TEXTVIEW_ID = 65569
    private final val NETFLIX_LOGIN_EMAIL_INPUT_TEXTVIEW_ID = 33
    private final val INSTAGRAM_LOGIN_USERNAME_INPUT_TEXTVIEW_ID = 1
    private final val LINKEDIN_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID = 129
    private final val NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID = 129

    //    @Inject
//    lateinit var passwordUseCases: PasswordUseCases
    override fun onConnected() {
        super.onConnected()
    }

    var hasDataSet: Boolean = false

    private val TAG: String = "KeysAutofillService"

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {

        val inlineRequest = request.inlineSuggestionsRequest

        val fillContexts = request.fillContexts
        val structure = fillContexts.last().structure
        val packageName = structure.activityComponent.packageName
        println("$TAG onFillRequest")
        Log.d(TAG, " packageName: $packageName")
        Log.d(TAG, " className: ${structure.activityComponent.className}")
        Log.d(TAG, " className: ${structure.activityComponent.shortClassName}")
//        getPasswords()

        var userNameId: AutofillId? = null
        var passwordId: AutofillId? = null

        println("AUTOFILL_HINT_USERNAME: ${View.AUTOFILL_HINT_USERNAME}")
        println("AUTOFILL_HINT_PASSWORD: ${View.AUTOFILL_HINT_PASSWORD}")

        // Traverse the structure and find the AutofillIds
        println("userNameId: $userNameId")
        println("passwordId: $passwordId")
        traverseStructure(
            structure,
            requestType = RequestType.OnFillRequest,
            setUsernameId = { _userNameId ->
                userNameId = _userNameId
                println("userNameId: $userNameId")

            },
            setPasswordId = { _passwordId ->
                passwordId = _passwordId
                println("passwordId: $passwordId")
            }
        )

        //create presentation for the dataset
        val presentation =
            RemoteViews(applicationContext.packageName, R.layout.your_auth_layout)

        val intent1 = Intent(applicationContext, BiometricAuthActivity::class.java)
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent1.putExtra(EXTRA_CLIENT_PACKAGE_NAME, packageName.replace('.', '_'))
        intent1.putExtra(EXTRA_AUTOFILL_IDS, arrayListOf(userNameId, passwordId))
        // Create a PendingIntent from the intent
        val pendingIntent1 = PendingIntent.getActivity(
            applicationContext, 0, intent1,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val intentSender1 = pendingIntent1.intentSender

        val responseBuilder = FillResponse.Builder()
        if (userNameId != null && passwordId != null) {
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
            callback.onSuccess(responseBuilder.build())
        } else if (userNameId != null) {
            responseBuilder.setAuthentication(arrayOf(userNameId), intentSender1, presentation)
            callback.onSuccess(responseBuilder.build())
        } else if (passwordId != null) {
            responseBuilder.setAuthentication(arrayOf(passwordId), intentSender1, presentation)
            callback.onSuccess(responseBuilder.build())
        } else {
            Toast.makeText(applicationContext, "No AutoFill fields found. ", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun traverseViewStructure(node: ViewNode, action: (ViewNode) -> Unit) {
        action(node)
        for (i in 0 until node.childCount) {
            traverseViewStructure(node.getChildAt(i), action)
        }
    }

    private fun traverseStructure(
        structure: AssistStructure,
        requestType: RequestType,
        setUsernameId: ((AutofillId) -> Unit)? = null,
        setPasswordId: ((AutofillId) -> Unit)? = null,
        setUsernameValue: ((String) -> Unit)? = null,
        setPasswordValue: ((String) -> Unit)? = null
    ) {
        val loginClassName = structure.activityComponent.shortClassName
        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode

            var foundUsernameLabel = false
            var foundPasswordLabel = false

            traverseViewStructure(viewNode) { node ->
                node.idEntry?.let { nodeIdEntry ->
                    //this is used previous view of textField is contains the hints or not
                    if (nodeIdEntry.contains("user") || nodeIdEntry.contains("email") && node.inputType == 0) {
                        foundUsernameLabel = true
                        println("username or email found")
                    }else if (nodeIdEntry.contains("user") || nodeIdEntry.contains("email") && node.inputType == INSTAGRAM_LOGIN_USERNAME_INPUT_TEXTVIEW_ID) {
                        foundUsernameLabel = true
                        println("username or email found")
                    }else if (node.inputType == NETFLIX_LOGIN_EMAIL_INPUT_TEXTVIEW_ID){
                        foundUsernameLabel = true
                        println("netflix username or email found")
                    }
                    if (nodeIdEntry.contains("pass") && node.inputType == 0) {
                        foundPasswordLabel = true
                        println("password found")
                    }else if (nodeIdEntry.contains("pass") && node.inputType == NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID) {
                        foundPasswordLabel = true
                        println("password found")
                    }else if (node.inputType == NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID){
                        foundUsernameLabel = true
                        println("netflix password found")
                    }
                }
                node.htmlInfo?.attributes?.forEach { pair ->
                    when (pair.first) {
                        "label" -> {
                            if (pair.second.contains("Username", ignoreCase = true)) {
                                if (setUsernameId != null && requestType == RequestType.OnFillRequest) {
                                    setUsernameId(node.autofillId!!)
                                }
                                if (setUsernameValue != null && requestType == RequestType.OnSaveRequest) {
                                    val value = node.autofillValue
                                    if (value?.isText!! && value?.textValue != null) {
                                        setUsernameValue(value.textValue.toString())
                                    }
                                }
                                // This is a username field
                            } else if (pair.second.contains("Password", ignoreCase = true)) {
                                if (setPasswordId != null && requestType == RequestType.OnFillRequest) {
                                    setPasswordId(node.autofillId!!)
                                }
                                if (setPasswordValue != null && requestType == RequestType.OnSaveRequest) {
                                    val value = node.autofillValue
                                    if (value?.isText!! && value?.textValue != null) {
                                        setPasswordValue(value.textValue.toString())
                                    }
                                }
                                // This is a password field
                            }
                        }
                        // ... You can add more cases
                    }
                }

                println("11: ${Arrays.toString(node.autofillHints)}")
                println("1: ${node.autofillId}, 2: ${node.autofillHints} 3: ${node.idEntry} 4: ${node.htmlInfo?.tag} 5: ${node.htmlInfo?.attributes} 6: ${node.inputType}")

                //this check if autofill is then go with 1st block otherwise go with second block
                if (node.autofillHints?.contains(View.AUTOFILL_HINT_USERNAME) == true) {
                    if (setUsernameId != null && requestType == RequestType.OnFillRequest) {
                        setUsernameId(node.autofillId!!)
                    }
                    if (setUsernameValue != null && requestType == RequestType.OnSaveRequest) {
                        val value = node.autofillValue
                        if (value?.isText!! && value?.textValue != null) {
                            setUsernameValue(value.textValue.toString())
                        }
                    }
                } else if (foundUsernameLabel && (node.inputType == InputType.TYPE_CLASS_TEXT || node.inputType == LINKEDIN_LOGIN_EMAIL_INPUT_TEXTVIEW_ID)) {
                    if (setUsernameId != null && requestType == RequestType.OnFillRequest) {
                        setUsernameId(node.autofillId!!)
                    }
                    if (setUsernameValue != null && requestType == RequestType.OnSaveRequest) {
                        val value = node.autofillValue
                        if (value?.isText!! && value?.textValue != null) {
                            setUsernameValue(value.textValue.toString())
                        }
                    }
                    foundUsernameLabel = false
                }else if (foundUsernameLabel && node.inputType == NETFLIX_LOGIN_EMAIL_INPUT_TEXTVIEW_ID){ //for netflix like app with not autofill hints provided
                    if (setUsernameId != null && requestType == RequestType.OnFillRequest) {
                        setUsernameId(node.autofillId!!)
                    }
                    if (setUsernameValue != null && requestType == RequestType.OnSaveRequest) {
                        val value = node.autofillValue
                        if (value?.isText!! && value?.textValue != null) {
                            setUsernameValue(value.textValue.toString())
                        }
                    }
                }

                if (node.autofillHints?.any {
                        it.contains(
                            View.AUTOFILL_HINT_PASSWORD,
                            ignoreCase = true
                        )
                    } == true) {
                    if (setPasswordId != null && requestType == RequestType.OnFillRequest) {
                        setPasswordId(node.autofillId!!)
                    }
                    if (setPasswordValue != null && requestType == RequestType.OnSaveRequest) {
                        val value = node.autofillValue
                        if (value?.isText!! && value?.textValue != null) {
                            setPasswordValue(value.textValue.toString())
                        }
                    }
                } else if (foundPasswordLabel && (node.inputType == InputType.TYPE_CLASS_TEXT || node.inputType == LINKEDIN_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID)) {
                    if (setPasswordId != null && requestType == RequestType.OnFillRequest) {
                        setPasswordId(node.autofillId!!)
                    }
                    if (setPasswordValue != null && requestType == RequestType.OnSaveRequest) {
                        val value = node.autofillValue
                        if (value?.isText!! && value?.textValue != null) {
                            setPasswordValue(value.textValue.toString())
                        }
                    }
                    foundPasswordLabel = false
                }else if (foundPasswordLabel && node.inputType == NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID){ //for netflix like app with not autofill hints provided
                    if (setUsernameId != null && requestType == RequestType.OnFillRequest) {
                        setUsernameId(node.autofillId!!)
                    }
                    if (setUsernameValue != null && requestType == RequestType.OnSaveRequest) {
                        val value = node.autofillValue
                        if (value?.isText!! && value?.textValue != null) {
                            setUsernameValue(value.textValue.toString())
                        }
                    }
                }
            }
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
        traverseStructure(
            structure,
            requestType = RequestType.OnSaveRequest,
            setUsernameValue = { userNameValue ->
                println("userNameId: $userNameValue")
                userName = userNameValue
            }, setPasswordValue = { passwordValue ->
                println("passwordId: $passwordValue")
                password = passwordValue
            })

        if (password?.isNotEmpty() == true && userName?.isNotEmpty() == true) {
            val intent1 = Intent(applicationContext, SaveAutofillPasswordActivity::class.java)
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent1.putExtra(EXTRA_PASSWORD_DATA, arrayListOf(userName, password, packageName, ""))
            applicationContext.startActivity(intent1)
        }

        callback.onSuccess()
    }

}

enum class RequestType {
    OnFillRequest, OnSaveRequest
}
