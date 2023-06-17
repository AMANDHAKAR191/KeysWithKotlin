package com.aman.keyswithkotlin.core

import android.util.Log
import com.aman.keyswithkotlin.core.Constants.TAG

class Utils {
    companion object {
        fun print(e: Exception) = Log.e(TAG, e.stackTraceToString())
    }
}