package com.aman.keys.notification_service

import android.app.Activity
import android.content.Context
import com.aman.keys.Keys
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class FCMNotificationSender(
    var userFcmToken: String,
    var from: String,
    var body: String,
    var mContext: Context,
    var mActivity: Activity
) {
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    fun sendNotification() {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", from)
            notiObject.put("message", body)
            notiObject.put("type", CustomMessagingService.BIG_TEXT)
            mainObj.put("data", notiObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                postUrl,
                mainObj,
                Response.Listener<JSONObject?> { response ->
                },
                Response.ErrorListener { error ->
                    // code run is got error
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=${Keys.fcm_server_key}"
                    return header
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}