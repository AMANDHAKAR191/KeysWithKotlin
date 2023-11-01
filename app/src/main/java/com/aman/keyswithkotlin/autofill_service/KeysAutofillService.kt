package com.aman.keyswithkotlin.autofill_service

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.text.InputType
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast
import com.aman.keyswithkotlin.R
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_AUTOFILL_HINTS
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_AUTOFILL_IDS
import com.aman.keyswithkotlin.autofill_service.BiometricAuthActivity.Companion.EXTRA_CLIENT_PACKAGE_NAME
import com.aman.keyswithkotlin.autofill_service.SaveAutofillPasswordActivity.Companion.EXTRA_PASSWORD_DATA
import com.aman.keyswithkotlin.passwords.domain.model.Password
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.flow
import java.util.ArrayList
import java.util.Arrays
import java.util.Locale


class KeysAutofillService : AutofillService() {

    private final val LINKEDIN_LOGIN_EMAIL_INPUT_TEXTVIEW_ID = 65569
    private final val NETFLIX_LOGIN_EMAIL_INPUT_TEXTVIEW_ID = 33
    private final val INSTAGRAM_LOGIN_USERNAME_INPUT_TEXTVIEW_ID = 1
    private final val LINKEDIN_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID = 129
    private final val NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID = 129

    override fun onConnected() {
        super.onConnected()
        println("onConnected")
    }

    override fun onDisconnected() {
        super.onDisconnected()
        println("onDisconnected")
    }


    var hasDataSet: Boolean = false
    private val mAuthenticateResponses: Boolean = true
    private val passwordList = mutableListOf(
        Password(
            "user1",
            "pass1",
            "example1.com",
            listOf("https://www.example1.com"),
            "timestamp1",
            "lastUsed1"
        ),
        Password(
            "user2",
            "pass2",
            "example2.com",
            listOf("https://www.example2.com"),
            "timestamp2",
            "lastUsed2"
        ),
        Password(
            "user3",
            "pass3",
            "example3.com",
            listOf("https://www.example3.com"),
            "timestamp3",
            "lastUsed3"
        ),
        Password(
            "user2",
            "pass2",
            "example2.com",
            listOf("https://www.example2.com"),
            "timestamp2",
            "lastUsed2"
        ),
        Password(
            "user3",
            "pass3",
            "example3.com",
            listOf("https://www.example3.com"),
            "timestamp3",
            "lastUsed3"
        )
    )
    private val TAG: String = "KeysAutofillService"

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        println("$TAG onFillRequest")
        // Find autofillable fields
        val structure1: AssistStructure = getLatestAssistStructure(request)
        val packageName = structure1.activityComponent.packageName
        println("$TAG onFillRequest")
        Log.d(TAG, " packageName: $packageName")
        Log.d(TAG, " className: ${structure1.activityComponent.className}")
        Log.d(TAG, " className: ${structure1.activityComponent.shortClassName}")
        val fields: ArrayMap<String, AutofillId> = getAutofillableFields(structure1)
        Log.d(TAG, "autofillable fields:$fields")

        if (fields.isEmpty()) {
            toast("No autofill hints found");
            callback.onSuccess(null);
            return;
        }


        val response: FillResponse.Builder

        if (mAuthenticateResponses) {
            val size = fields.size
            val hints = arrayOfNulls<String>(size)
            val ids = arrayOfNulls<AutofillId>(size)
            for (i in 0 until size) {
                hints[i] = fields.keyAt(i)
                ids[i] = fields.valueAt(i)
            }

//            val authentication = BiometricAuthActivity.newIntentSenderForResponse(this, hints,
//                ids, false);

            //create presentation for the dataset
            val presentation =
                RemoteViews(applicationContext.packageName, R.layout.your_auth_layout)


//            response = FillResponse.Builder()
//                .setAuthentication(ids, authentication, presentation)

            val intent1 = Intent(applicationContext, BiometricAuthActivity::class.java)
            intent1.putExtra(EXTRA_CLIENT_PACKAGE_NAME, packageName)
            intent1.putExtra(EXTRA_AUTOFILL_HINTS, arrayListOf(hints[0], hints[1]))
            intent1.putExtra(EXTRA_AUTOFILL_IDS, arrayListOf(ids[0], ids[1]))
            // Create a PendingIntent from the intent
            val pendingIntent1 = PendingIntent.getActivity(
                applicationContext, 0, intent1,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            val intentSender1 = pendingIntent1.intentSender
            response = FillResponse.Builder()
                .setAuthentication(ids, intentSender1, presentation)
        } else {
            response = createResponse(this, fields, 4,passwordList.toList(), false)
        }
        println("autofillable fields: $fields")
        val userNameId = fields[fields.keyAt(0)]
        val passwordId = fields[fields.keyAt(1)]
        println("userNameId: $userNameId, passwordId: $passwordId")
        response.setSaveInfo(
            SaveInfo.Builder(
                SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                arrayOf(userNameId, passwordId)
            ).build()
        )
        callback.onSuccess(response.build())
    }

    fun createResponse(
        context: Context,
        fields: ArrayMap<String, AutofillId>,
        numDatasets: Int,
        passwordList:List<Password>,
        authenticateDatasets: Boolean
    ): FillResponse.Builder {
        val packageName = context.packageName
        val response = FillResponse.Builder()
        // 1.Add the dynamic datasets
        for (i in 1..passwordList.size) {
            val unlockedDataset: Dataset = newUnlockedDataset(fields, packageName, i, passwordList)
            response.addDataset(unlockedDataset)
        }

//        // 2.Add save info
//        val ids: Collection<AutofillId> = fields.values
//        val requiredIds = arrayOfNulls<AutofillId>(ids.size)
//        ids.toTypedArray()
//        response.setSaveInfo( // We're simple, so we're generic
//            SaveInfo.Builder(SaveInfo.SAVE_DATA_TYPE_GENERIC, requiredIds).build()
//        )

        // 3.Profit!
        return response
    }

    private fun newUnlockedDataset(
        fields: Map<String, AutofillId>,
        packageName: String, i: Int,
        passwordList:List<Password>
    ): Dataset {
        val dataset = Dataset.Builder()
        for (field in fields) {
            val hint = field.key
            val id = field.value
            val username = passwordList[i-1].userName
            val password = passwordList[i-1].password

            // We're simple - our dataset values are hardcoded as "N-hint" (for example,
            // "1-username", "2-username") and they're displayed as such, except if they're a
            // password
            val displayValue = if (hint.contains("password")) "password for $username" else username
            val presentation: RemoteViews = newDatasetPresentation(packageName, displayValue)
            dataset.setValue(
                id,
                if (hint.contains("password")) AutofillValue.forText(password) else AutofillValue.forText(username),
                presentation)
        }
        return dataset.build()
    }

    /**
     * Helper method to get the [AssistStructure] associated with the latest request
     * in an autofill context.
     */
    private fun getLatestAssistStructure(request: FillRequest): AssistStructure {
        val fillContexts = request.fillContexts
        return fillContexts[fillContexts.size - 1].structure
    }

    /**
     * Helper method to create a dataset presentation with the given text.
     */
    private fun newDatasetPresentation(
        packageName: String,
        text: CharSequence
    ): RemoteViews {
        val presentation = RemoteViews(packageName, R.layout.autofill_list_item)
        presentation.setTextViewText(R.id.text, text)
        presentation.setImageViewResource(R.id.icon, R.mipmap.ic_launcher_keys_lock)
        return presentation
    }

    /**
     * Parses the [AssistStructure] representing the activity being autofilled, and returns a
     * map of autofillable fields (represented by their autofill ids) mapped by the hint associate
     * with them.
     *
     *
     * An autofillable field is a [ViewNode] whose [.getHint] metho
     */
    private fun getAutofillableFields(structure: AssistStructure): ArrayMap<String, AutofillId> {
        val fields = ArrayMap<String, AutofillId>()
        val nodes = structure.windowNodeCount
        for (i in 0 until nodes) {
            val node = structure.getWindowNodeAt(i).rootViewNode
            addAutofillableFields(fields, node)
        }
        return fields
    }

    /**
     * Adds any autofillable view from the [ViewNode] and its descendants to the map.
     */
    private fun addAutofillableFields(
        fields: MutableMap<String, AutofillId?>,
        node: ViewNode,
    ) {
        val hint: String? = getHint(node)
        if (hint != null) {
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
        } else {

        }
        val childrenSize = node.childCount
        for (i in 0 until childrenSize) {
            addAutofillableFields(fields, node.getChildAt(i))
        }
    }

    private fun getHint(node: ViewNode): String? {
        println("check1: input_type: ${node.inputType}")
        // First try the explicit autofill hints...
        val hints = node.autofillHints
        if (hints != null) {
            // We're simple, we only care about the first hint
            if (hints.contains("username") || hints.contains("email") || hints.contains("password")) {
                println("check1.1: $hints")
                return hints[0].lowercase(Locale.getDefault())
            }
        }

        // Then try some rudimentary heuristics based on other node properties
        val viewHint = node.hint
        println("hint: $viewHint")
        var hint: String? = inferHint(node, viewHint)
        if (hint != null) {
            println("check2")
            Log.d(TAG, "Found hint using view hint($viewHint): $hint")
            return hint
        } else if (!TextUtils.isEmpty(viewHint)) {
            println("check2.1")
            Log.v(TAG, "No hint using view hint: $viewHint")
        }
        val resourceId = node.idEntry
        println("resourceId: $resourceId")
        hint = inferHint(node, resourceId)
        if (hint != null) {
            println("check3")
            Log.d(TAG, "Found hint using resourceId($resourceId): $hint")
            return hint
        } else if (!TextUtils.isEmpty(resourceId)) {
            println("check3.1")
            Log.v(TAG, "No hint using resourceId: $resourceId")
        }
//        val text = node.text
//        val className: CharSequence? = node.className
//        if (text != null && className != null && className.toString().contains("EditText")) {
//            hint = inferHint(node, text.toString())
//            if (hint != null) {
//                // NODE: text should not be logged, as it could contain PII
//                Log.d(TAG, "Found hint using text($text): $hint")
//                return hint
//            }
//        } else if (!TextUtils.isEmpty(text)) {
//            // NODE: text should not be logged, as it could contain PII
//            Log.v(TAG, "No hint using text: $text and class $className")
//        }
        return null
    }

    /**
     * Uses heuristics to infer an autofill hint from a `string`.
     *
     * @return standard autofill hint, or `null` when it could not be inferred.
     */
    private fun inferHint(node: ViewNode, actualHint: String?): String? {
        if (actualHint == null) return null
        val hint = actualHint.lowercase(Locale.getDefault())
        val id = node.inputType
        if (hint.contains("label") || hint.contains("container")) {
            Log.v(TAG, "Ignoring 'label/container' hint: $hint")
            return null
        }
        if (hint.contains("password")) return View.AUTOFILL_HINT_PASSWORD
        if (hint.contains("username") || hint.contains("login") && hint.contains("id")) return View.AUTOFILL_HINT_USERNAME
        if (hint.contains("email") || hint.contains("email")) return View.AUTOFILL_HINT_EMAIL_ADDRESS
        if (hint.contains("name")) return View.AUTOFILL_HINT_NAME
        if (hint.contains("phone")) return View.AUTOFILL_HINT_PHONE


        // When everything else fails, return the full string - this is helpful to help app
        // developers visualize when autofill is triggered when it shouldn't (for example, in a
        // chat conversation window), so they can mark the root view of such activities with
        // android:importantForAutofill=noExcludeDescendants
        if (node.isEnabled && node.autofillType != View.AUTOFILL_TYPE_NONE) {
            println("input_type: ${node.inputType}, attribute: ${node.htmlInfo?.attributes}")
            node.htmlInfo?.attributes?.forEach { pair ->
                println("inner check: ${pair.first}, ${pair.second}")
                when (pair.first) {
                    "label" -> {
                        println("inner check 1")
                        if (pair.second.contains("Username", ignoreCase = true)) {
                            Log.v(TAG, "assigning new hint username")
                            return View.AUTOFILL_HINT_USERNAME
                            // This is a username field
                        } else if (pair.second.contains("Password", ignoreCase = true)) {
                            Log.v(TAG, "assigning new hint password")
                            return View.AUTOFILL_HINT_PASSWORD
                            // This is a password field
                        }
                    }
                    // ... You can add more cases
                }
            }
            Log.v(TAG, "Falling back to $actualHint")
            //removing hint other then our requirement.
            return null
        }
        return null
    }

    /**
     * Displays a toast with the given message.
     */
    private fun toast(message: CharSequence) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
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
                    } else if (nodeIdEntry.contains("user") || nodeIdEntry.contains("email") && node.inputType == INSTAGRAM_LOGIN_USERNAME_INPUT_TEXTVIEW_ID) {
                        foundUsernameLabel = true
                        println("username or email found")
                    } else if (node.inputType == NETFLIX_LOGIN_EMAIL_INPUT_TEXTVIEW_ID) {
                        foundUsernameLabel = true
                        println("netflix username or email found")
                    }
                    if (nodeIdEntry.contains("pass") && node.inputType == 0) {
                        foundPasswordLabel = true
                        println("password found")
                    } else if (nodeIdEntry.contains("pass") && node.inputType == NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID) {
                        foundPasswordLabel = true
                        println("password found")
                    } else if (node.inputType == NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID) {
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
                } else if (foundUsernameLabel && node.inputType == NETFLIX_LOGIN_EMAIL_INPUT_TEXTVIEW_ID) { //for netflix like app with not autofill hints provided
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
                } else if (foundPasswordLabel && node.inputType == NETFLIX_LOGIN_PSSWORD_INPUT_TEXTVIEW_ID) { //for netflix like app with not autofill hints provided
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
