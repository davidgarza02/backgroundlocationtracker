package com.example.backgroundlocationtracker.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(): Flow<Location>

    class LocationException(message: String): Exception(message)
}