package com.example.smslock

import android.app.Notification
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.widget.Toast

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (packageName == "com.google.android.apps.messaging") { // Google Messages app
            val extras = sbn.notification.extras
            val message = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()

            // Detect if this message matches your criteria (e.g., comes from the specific number)
            if (message?.contains("lockmenow") == true) {
                // Trigger lockPhone
                lockPhone()
            }
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
