package com.example.smslock

import android.app.Notification
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf


class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (packageName == "com.google.android.apps.messaging") { // Google Messages app
            val extras = sbn.notification.extras
            val message = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()

            val inputData = workDataOf("sms_message" to message)
            val smsWorkRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                .setInputData(inputData)
                .build()
            Log.d("NotificationService", "Enqueuing SMS work request");
            WorkManager.getInstance(this).enqueue(listOf(smsWorkRequest))

//            val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
//            val lockMessage = sharedPrefs.getString("lock_message", "default message")
//            // Detect if this message matches your criteria (e.g., comes from the specific number)
//            if (message?.contains(lockMessage.toString()) == true) {
//                // Trigger lockPhone
//                lockPhone()
//            }
        }
    }

    private fun lockPhone() {
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val compName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        if (devicePolicyManager.isAdminActive(compName)) {
            devicePolicyManager.lockNow()
        } else {
            Toast.makeText(this, "Please activate admin rights", Toast.LENGTH_SHORT).show()
        }
    }
}
