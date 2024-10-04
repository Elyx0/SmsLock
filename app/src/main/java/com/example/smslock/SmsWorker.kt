package com.example.smslock
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import java.util.Date


class SmsWorker(appContext: Context, workerParams: androidx.work.WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Get the SMS message from inputData
        val message = inputData.getString("sms_message")

        if (message != null) {
            // Process the SMS message here (e.g., check for keywords, lock the device)
            val sharedPrefs = applicationContext.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val lockMessage = sharedPrefs.getString("lock_message", "lockmenow")

            if (!message.contains(lockMessage.toString())) { return Result.success() }
            Log.d("SmsWorker", "Message has lock");

            val delay = sharedPrefs.getLong("delay_ms", 3*60*1000L)
            val currentTimestamp = System.currentTimeMillis()
            val lastLock = sharedPrefs.getLong("last_lock", 0)
            if (currentTimestamp - lastLock > delay) {
                Log.d("SmsWorker", "Delay elapsed");

                // Edit shared pref to store last time of lock
                val editor = sharedPrefs.edit()
                editor.putLong("last_lock", currentTimestamp)
                editor.apply()
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