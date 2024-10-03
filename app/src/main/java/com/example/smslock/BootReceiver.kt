package com.example.smslock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("BootReceiver", "Received boot completed intent")
            val smsWorkRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                .build()
            androidx.work.WorkManager.getInstance(context).enqueue(smsWorkRequest)
        }
    }
}