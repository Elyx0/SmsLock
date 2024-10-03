package com.example.smslock

import android.Manifest
import android.app.Activity
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.smslock.ui.theme.SmsLockTheme

//class MainActivity : ComponentActivity() {
//    private val REQUEST_CODE_ENABLE_ADMIN = 1
//    private val REQUEST_CODE_RECEIVE_SMS = 101
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        println("onCreate")
//        Log.d("MainActivity", "onCreate")
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            SmsLockTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//        println("PRE Requesting device admin permission")
//        checkSmsPermission()
//    }
//
//    private fun checkSmsPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.RECEIVE_SMS),
//                REQUEST_CODE_RECEIVE_SMS
//            )
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_RECEIVE_SMS) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//            } else {
//                // Permission denied
//            }
//        }
//    }
//
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SmsLockTheme {
//        Greeting("Android")
//    }
//}

import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private val RESULT_ENABLE = 1
    private val SMS_PERMISSION_CODE = 101


    fun lockPhone() {
        if (devicePolicyManager.isAdminActive(compName)) {
            devicePolicyManager.lockNow() // Verrouille immédiatement l'appareil
        } else {
            Toast.makeText(this, "Requires Admin Privileges", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    // Gérer la réponse de l'utilisateur pour les permissions
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
