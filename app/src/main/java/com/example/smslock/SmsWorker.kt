package com.example.smslock
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getSystemService


class SmsWorker(appContext: Context, workerParams: androidx.work.WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Get the SMS message from inputData
        val message = inputData.getString("sms_message")

        if (message != null) {
            // Process the SMS message here (e.g., check for keywords, lock the device)
            if (message.contains("lockmenow")) {
                lockDevice(applicationContext)
            }
            return Result.success()
        } else {
            return Result.failure()
        }
    }

    private fun lockDevice(context: Context) {
        // Implement your screen locking logic here (using KeyguardManager or the workaround)
        Log.d("SmsReceiver", "Locking device");
        // Calls DeviceLockService to lock the device
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        devicePolicyManager.lockNow()

    }
}