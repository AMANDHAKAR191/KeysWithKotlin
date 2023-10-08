package com.aman.keyswithkotlin.core

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.util.Log

class AppLockCounterClass(
    private val myPreference: MyPreference,
    private val activity: Activity,
    private val context: Context,
    private val finishActivity: () -> Unit
) {
    private lateinit var countDownTimer: CountDownTimer
    private val TAG = "AppLockCounterClass"
    private var isCountDownTimeFinished = true
    private var biometricAuthentication: BiometricAuthentication =
        BiometricAuthentication(activity, context, finishActivity)

    fun initializeCounter() {
        isCountDownTimeFinished = false
        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(milliSecondCount: Long) {
                Log.d(TAG, "Counter Running:" + milliSecondCount / 1000);
            }

            override fun onFinish() {
                Log.d(TAG, "Counter Finished");
                isCountDownTimeFinished = true
//                launchBiometric()
            }

        }
    }

    fun onStartOperation() {
        when (myPreference.lockAppSelectedOption) {
            LockAppType.IMMEDIATELY.toString() -> {
                biometricAuthentication.launchBiometric()
            }

            LockAppType.AFTER_1_MINUTE.toString() -> {
                if (isCountDownTimeFinished) {
                    biometricAuthentication.launchBiometric()
                } else {
                    countDownTimer.cancel()
                }
            }

            LockAppType.NEVER.toString() -> {

            }
        }
    }

    fun onPauseOperation() {
        when (myPreference.lockAppSelectedOption) {
            LockAppType.IMMEDIATELY.toString() -> {
            }

            LockAppType.AFTER_1_MINUTE.toString() -> {
                countDownTimer.start()
            }

            LockAppType.NEVER.toString() -> {

            }
        }
    }


}