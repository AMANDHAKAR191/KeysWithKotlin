package com.aman.keyswithkotlin.notification_service

import android.app.Activity
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
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
    private val fcmServerKey =
        "AAAADjQBUvg:APA91bFwbsYgMatxJPuhsUCu39ALX5qrdpTIaYgKYCOulV_svsm-Fc9texM3XAjuy07jNJxU5w4PaDQViVUawuTFaqxB1TB8yLOoYNyGTW3AbXLf_6ImP8DdZ0GMyxuNmiU1Afn2ytK5"

    fun sendNotification() {
        println("sendNotification: check1")
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
                    println("FCM responses: $response")
                },
                Response.ErrorListener { error ->
                    println("FCM error: $error")
                    // code run is got error
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}