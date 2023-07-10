package com.aman.keyswithkotlin.autofill_service

import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.util.Log
import android.widget.Toast

class KeysAutofillService : AutofillService() {
    private val TAG: String = "KeysAutofillService"
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts[request.fillContexts.size - 1].structure
        val packageName = structure.activityComponent.packageName
        println("packageName: $packageName")
        if (!PackageVerifier.isValidPackage(applicationContext, packageName)) {
            Toast.makeText(applicationContext, "Invalid Package name", Toast.LENGTH_SHORT).show()
            return
        }
        val data = request.clientState
        Log.d(TAG, "onFillRequest(): data=  ${CommonUtil.bundleToString(data)})")
        cancellationSignal.setOnCancelListener { }
        //write a basic sample code here

    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        TODO("Not yet implemented")
    }

}