package com.example.backgroundlocationtracker.worker

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.backgroundlocationtracker.R
import com.example.backgroundlocationtracker.location.BackgroundTrackerLocationClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class PeriodicTrackerWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    companion object {
        val COLLECT_LIMIT = 3
    }

    private val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //Task #3:
    //Using fuse location to get location updates because combines network and GPS, has handy functions to modify
    //and fine tune the location updates we need like intervals, easy to convert to flow with callbackFlow
    override fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }
        val locationClient = BackgroundTrackerLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        locationClient
            .getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .take(COLLECT_LIMIT)
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                workerScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.getString(R.string.location_toast, lat, long),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                /*Task #4:
                *TODO: Store location updates, we have at least 3 main options to store data locally,
                * Room database (SQLite): best to use when dealing with large data sets, relationships, tables, queries, if we want to search or process data later,
                * also is converted to entities/objects out of the box.
                * SharedPreferences: store data easily just 1-2 lines of code, key - value format, we also could store JSON Strings but we would have to deal with
                * serialize and deserialize or have a library like Jackson/Gson to handle this for us.
                * ExternalStorage: We could write locations in a file.txt use JSON format or CSV, etc. could be useful if we want to use this later with a text
                * editor or we want to parse it manually, the less secure of the options, unless we encrypt/decrypt the data, could be easy to handle by the user if they
                * want to move their location data to another phone or store in cloud like drive or dropbox, we could easy import export this file.
                * */
            }.onCompletion {
                workerScope.cancel()
            }
            .launchIn(workerScope)
        return Result.success()
    }

}