package com.example.smslock

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private val RESULT_ENABLE = 1
    private val SMS_PERMISSION_CODE = 101

    fun isNotificationServiceEnabled(context: Context): Boolean {
        val packageNames = Settings.Secure.getString(
            context.contentResolver, "enabled_notification_listeners"
        )
        return packageNames != null && packageNames.contains(context.packageName)
    }

    fun requestNotificationListenerPermission(context: Context) {
        if (!isNotificationServiceEnabled(context)) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNotificationServiceEnabled(this)) {
            // Notification listener permission is enabled, proceed with the service
            Toast.makeText(this, "Permission Notification ok", Toast.LENGTH_SHORT).show()

        } else {
            // Permission is not granted, ask again or handle accordingly
            Toast.makeText(this, "Permission Notification fail", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       if (!isNotificationServiceEnabled(this)) {
           AlertDialog.Builder(this)
               .setTitle("Enable Notification Access")
               .setMessage("This app needs access to your notifications to detect incoming messages and perform certain actions like locking the device. Please enable notification access in the next screen.")
               .setPositiveButton("OK") { dialog, _ ->
                   requestNotificationListenerPermission(this)
               }
               .setNegativeButton("Cancel", null)
               .show()
       }
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        // Find R.id.btnActivateAdmin
        val btnActivateAdmin: Button = findViewById(R.id.btnActivateAdmin)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Requesting SMS permission")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                SMS_PERMISSION_CODE)
        } else {
            Log.d("MainActivity", "SMS permission already granted")
        }

        btnActivateAdmin.setOnClickListener {
            Log.d("MainActivity", "Button clicked")
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Requires admin privileges with lock.")
            startActivityForResult(intent, RESULT_ENABLE)
        }

        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val safeWord = findViewById<EditText>(R.id.editTextText)
        val delay = findViewById<EditText>(R.id.editTextText2)

        safeWord.setText(sharedPrefs.getString("lock_message", "lockmenow"))
        delay.setText((sharedPrefs.getLong("delay_ms", 3*60*1000L) / 60000L).toString())

        // Find R.id.save
        val btnSave: Button = findViewById(R.id.save)
        btnSave.setOnClickListener {
            Log.d("MainActivity", "Save button clicked")
            if (safeWord.text.toString().isEmpty() || delay.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Save the safe word and delay to SharedPreferences
                val editor = sharedPrefs.edit()
                editor.putString("lock_message", safeWord.getText().toString())
                editor.putLong("delay_ms", delay.getText().toString().toLong() *60000L)
                editor.apply()
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission SMS ok", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission SMS refued", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_ENABLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Admin permissions enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failure to activate", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
