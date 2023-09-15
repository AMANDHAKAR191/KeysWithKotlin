package com.aman.keyswithkotlin.core

import android.content.Context
import android.content.SharedPreferences
import com.aman.keyswithkotlin.Keys

class MyPreference() {
//    var context = _context!!
    var sharedPreferences: SharedPreferences
    val SHARED_PREF_KEYS = "keysSharedPreference"
    val AES_CLOUD_KEY = "aesCloudKey"
    val AES_CLOUD_IV = "aesCloudIv"
    val AES_LOCAL_KEY = "aesLocalKey"
    val AES_LOCAL_IV = "aesLocalIv"
    val PUBLIC_UID = "publicUid"
    val LOCK_APP_OPTIONS = "lock_app"
    val ROOM_ID = "receiverRoomId"
    val RECEIVER_PUBLIC_UID = "receiverPublicId"
    val USER_RESTRICTED = "userRestricted"
    val USER_AUTHENTICATED = "userAuthenticated"
    val COMMON_CHAT_ROOM_ID = "commonChatRoomId"
    val PRIMARY_USER_DEVICE_ID = "primaryUserDeviceId"

    init {
//        sharedPreferences =
//            context.applicationContext.getSharedPreferences(SHARED_PREF_KEYS, Context.MODE_PRIVATE)
        sharedPreferences = Keys.instance.getSharedPreferences(SHARED_PREF_KEYS, Context.MODE_PRIVATE)
    }

    var aesCloudKey: String
        get() = sharedPreferences.getString(AES_CLOUD_KEY, "")!!
        //String getter/setter method
        set(aesKey) {
            sharedPreferences.edit().putString(AES_CLOUD_KEY, aesKey).apply()
        }
    var aesCloudIv: String
        get() = sharedPreferences.getString(AES_CLOUD_IV, "")!!
        set(aesIv) {
            sharedPreferences.edit().putString(AES_CLOUD_IV, aesIv).apply()
        }

    var aesLocalKey: String
        get() = sharedPreferences.getString(AES_LOCAL_KEY, "")!!
        //String getter/setter method
        set(aesKey) {
            sharedPreferences.edit().putString(AES_LOCAL_IV, aesKey).apply()
        }
    var aesLocalIv: String
        get() = sharedPreferences.getString(AES_CLOUD_IV, "")!!
        set(aesIv) {
            sharedPreferences.edit().putString(AES_CLOUD_IV, aesIv).apply()
        }

    var publicUid: String?
        get() = sharedPreferences.getString(PUBLIC_UID, "")
        set(publicUid) {
            sharedPreferences.edit().putString(PUBLIC_UID, publicUid).apply()
        }

    var roomId: String?
        get() = sharedPreferences.getString(ROOM_ID, "")
        set(receiverRoomId) {
            sharedPreferences.edit().putString(ROOM_ID, receiverRoomId).apply()
        }

    var commonChatRoomId: String?
        get() = sharedPreferences.getString(COMMON_CHAT_ROOM_ID, "")
        set(receiverRoomId) {
            sharedPreferences.edit().putString(COMMON_CHAT_ROOM_ID, receiverRoomId).apply()
        }

    var receiverPublicId: String?
        get() = sharedPreferences.getString(RECEIVER_PUBLIC_UID, "")
        set(receiverRoomId) {
            sharedPreferences.edit().putString(RECEIVER_PUBLIC_UID, receiverRoomId).apply()
        }

    var isUserRestricted: Boolean
        get() = sharedPreferences.getBoolean(USER_RESTRICTED, false)
        set(isUserRestricted) {
            sharedPreferences.edit().putBoolean(USER_RESTRICTED, isUserRestricted).apply()
        }
    var isUserAuthenticated: Boolean
        get() = sharedPreferences.getBoolean(USER_AUTHENTICATED, false)
        set(isUserRestricted) {
            sharedPreferences.edit().putBoolean(USER_AUTHENTICATED, isUserRestricted).apply()
        }
    var lockAppSelectedOption: Int
        get() = sharedPreferences.getInt(LOCK_APP_OPTIONS, 0)
        //integer getter/setter method
        set(selectedOption) {
            sharedPreferences.edit().putInt(LOCK_APP_OPTIONS, selectedOption).apply()
        }

    var primaryUserDeviceId:String?
        get() = sharedPreferences.getString(PRIMARY_USER_DEVICE_ID,"")
        set(deviceId) {
            sharedPreferences.edit().putString(PRIMARY_USER_DEVICE_ID,deviceId).apply()
        }

//    companion object {
//        @SuppressLint("StaticFieldLeak")
//        private var sInstance: MyPreference? = null
//        fun getInstance(context: Context): MyPreference? {
//            if (sInstance == null) {
//                sInstance = MyPreference(context)
//            }
//            return sInstance
//        }
//    }
}