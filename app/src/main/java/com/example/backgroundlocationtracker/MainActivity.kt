package com.example.backgroundlocationtracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.backgroundlocationtracker.util.hasBackgroundLocationPermission
import com.example.backgroundlocationtracker.util.hasRegularLocationPermissions
import com.example.backgroundlocationtracker.util.linkify
import com.example.backgroundlocationtracker.worker.PeriodicTrackerWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        val REQ_CODE_BG_LOCATION_PERMISSIONS = 922
        val DEBUG_INTERVAL_WORK_MANAGER = 15L
        val RELEASE_INTERVAL_WORK_MANAGER = 60L
        val FLEX_INTERVAL_WORK_MANAGER = 10L
        val TAG_BACKGROUND_LOCATION_TRACKER = "TAG_BACKGROUND_LOCATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Task #1
        makeHelloClickable()

        requestLocationPermissions()
    }

    //Task #1: Makes click on substring "Hello" create and show a toast.
    private fun makeHelloClickable() {
        val helloWorldTv = findViewById<TextView>(R.id.id_text_view)
        helloWorldTv.linkify(substring = "Hello", action = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(widget.context, "Click on Hello!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*Task #2: Using workManager to start the scheduled work because, provides backward compatibility, depending on the ApiVersion it can use
     *JobScheduler, FirebaseScheduler or AlarmScheduler, can be monitored and observed, provides guarantees that the work will be performed
     *even when phone is restarted and handles low/saving battery mode, Doze mode, and some other constraints automatically  */
    private fun startWorkManager() {
        val workManager = WorkManager.getInstance(this)
        val repeatInterval =
            if (BuildConfig.DEBUG) DEBUG_INTERVAL_WORK_MANAGER else RELEASE_INTERVAL_WORK_MANAGER
        val periodicTracker = PeriodicWorkRequestBuilder<PeriodicTrackerWorker>(
            repeatInterval = repeatInterval,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = FLEX_INTERVAL_WORK_MANAGER,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).addTag(TAG_BACKGROUND_LOCATION_TRACKER)
            .build()
        workManager.enqueueUniquePeriodicWork(
            TAG_BACKGROUND_LOCATION_TRACKER,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicTracker
        )
    }

    //Request regular location permissions (coarse and fine)
    private fun requestLocationPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false) ||
                        (permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false) -> {
                    requestBackgroundLocationPermission()
                }

                else -> {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_exception),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (!hasRegularLocationPermissions()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            locationPermissionRequest.launch(
                permissions.toTypedArray()
            )
        } else {
            requestBackgroundLocationPermission()
        }
    }

    //Request background location permission if has not been granted, remove inline lint since it is checked inside
    @SuppressLint("InlinedApi")
    private fun requestBackgroundLocationPermission() {
        if (!hasBackgroundLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                REQ_CODE_BG_LOCATION_PERMISSIONS
            )
        } else {
            startWorkManager()
        }
    }

    //Result called when request background location permission granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_BG_LOCATION_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWorkManager()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.permission_exception),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}