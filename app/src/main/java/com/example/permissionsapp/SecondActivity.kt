package com.example.permissionsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class SecondActivity : AppCompatActivity() {
    private lateinit var checkPermissionSecond: Button
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    @RequiresApi(Build.VERSION_CODES.R)
    private val permissions = arrayOf(
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.READ_CONTACTS
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        checkPermissionSecond = findViewById(R.id.checkPermissionSecond)

        setupPermissionsLauncher()

        checkPermissionSecond.setOnClickListener {
            checkAndRequestPermissions()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupPermissionsLauncher() {
        requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allPermissionsGranted = permissions.values.all { it }

                if (allPermissionsGranted) {
                    showToast("All permissions are granted...")
                } else {
                    handlePermissionsDenied(permissions)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkAndRequestPermissions() {
        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsNeeded)
        } else {
            showToast("Permissions are already granted...")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun handlePermissionsDenied(permissions: Map<String, Boolean>) {
        val rationaleNeeded = permissions.keys.any { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }

        if (rationaleNeeded) {
            showPermissionRationaleDialog()
        } else {
            showToast("Some permissions are denied permanently. Please grant them from settings.")
            openAppSettings()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Needed")
            .setMessage("This app requires specific permissions to function correctly. Please grant the permissions.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionsLauncher.launch(permissions)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
