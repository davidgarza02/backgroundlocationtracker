package com.example.backgroundlocationtracker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class PeriodicTrackerWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        //Task 3:
        //sendToast
        return Result.success()
    }

}