package com.example.permissionsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private lateinit var checkPermissionButton: Button
    private lateinit var nextButton: Button
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissionButton = findViewById(R.id.checkPermissionButton)
        nextButton = findViewById(R.id.nextButton)

        setupPermissionLauncher()

        checkPermissionButton.setOnClickListener {
            checkPermission()
        }

        nextButton.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    showToast("Permission granted...")
                } else {
                    handlePermissionDenied()
                }
            }
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                showToast("Permission already granted...")
            }

            else -> {
                requestPermissionLauncher.launch(CAMERA_PERMISSION)
            }
        }
    }

    private fun handlePermissionDenied() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION)) {
            showPermissionRationaleDialog()
        } else {
            showToast("Permission is denied forever. Go to settings and grant the permissions...")
            openSettings()
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("This app requires camera access to function properly. Please grant the camera permission.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(CAMERA_PERMISSION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}