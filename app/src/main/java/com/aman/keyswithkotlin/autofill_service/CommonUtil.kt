package com.aman.keyswithkotlin.autofill_service

import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.Arrays

object CommonUtil {

    val TAG = "AutofillSample"

    val EXTRA_DATASET_NAME = "dataset_name"
    val EXTRA_FOR_RESPONSE = "for_response"

    private fun bundleToString(builder: StringBuilder, data: Bundle) {
        val keySet = data.keySet()
        builder.append("[Bundle with ").append(keySet.size).append(" keys:")
        for (key in keySet) {
            builder.append(' ').append(key).append('=')
            val value = data.get(key)
            if (value is Bundle) {
                bundleToString(builder, value)
            } else {
                val string = if (value is Array<*>) Arrays.toString(value) else value
                builder.append(string)
            }
        }
        builder.append(']')
    }

    fun bundleToString(data: Bundle?): String {
        if (data == null) {
            return "N/A"
        }
        val builder = StringBuilder()
        bundleToString(builder, data)
        return builder.toString()
    }

    fun createGson(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
    }
}