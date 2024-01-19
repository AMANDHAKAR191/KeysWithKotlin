package com.aman.keys.core

import android.util.Log
import com.aman.keys.core.Constants.TAG

class Utils {
    companion object {
        fun print(e: Exception) = Log.e(TAG, e.stackTraceToString())
    }
}