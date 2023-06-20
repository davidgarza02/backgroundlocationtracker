package com.example.backgroundlocationtracker

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.backgroundlocationtracker.util.findFirstLastIndex
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object{
        val TAG_BACKGROUND_LOCATION_TRACKER = "TAG_BACKGROUND_LOCATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Task #1
        setOnClickHello()

        //Task #2
        startWorkManager()
    }


    /*Using workManager to start the scheduled work because, provides backward compatibility, depending on the ApiVersion it can use
     *JobScheduler, FirebaseScheduler or AlarmScheduler, can be monitored and observed, provides guarantees that the work will be performed
     *even when phone is restarted */
    private fun startWorkManager() {
        val workManager = WorkManager.getInstance(this)
        val repeatInterval = if (BuildConfig.DEBUG) 15L else 60L
        val periodicTracker = PeriodicWorkRequestBuilder<PeriodicTrackerWorker>(
            repeatInterval = repeatInterval,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).addTag(TAG_BACKGROUND_LOCATION_TRACKER)
            .build()
        workManager.enqueueUniquePeriodicWork(
            TAG_BACKGROUND_LOCATION_TRACKER,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicTracker
        )
    }

    //sets click on substring "Hello" of the text view
    private fun setOnClickHello() {
        val helloWorldTv = findViewById<TextView>(R.id.id_text_view)
        val tvText = helloWorldTv.text.toString()
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@MainActivity, "Hello Toast!", Toast.LENGTH_SHORT).show()
            }
        }
        val spannableString = SpannableString(tvText)
        val indexes = tvText.findFirstLastIndex(substring = "Hello")
        if (indexes.first > -1) {
            spannableString.setSpan(
                clickableSpan,
                indexes.first,
                indexes.second,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            helloWorldTv.text = spannableString
            helloWorldTv.highlightColor = Color.TRANSPARENT
            helloWorldTv.movementMethod = LinkMovementMethod.getInstance()
        }
    }

}