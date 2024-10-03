package com.example.smslock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SmsReceiver", "Received intent action: ${intent.action}");
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val messageBody = smsMessage.messageBody
                val inputData = workDataOf("sms_message" to messageBody)
                val smsWorkRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                    .setInputData(inputData)
                    .build()
                Log.d("SmsReceiver", "Enqueuing SMS work request");
                WorkManager.getInstance(context).enqueue(listOf(smsWorkRequest))
            }
        }
    }
}
