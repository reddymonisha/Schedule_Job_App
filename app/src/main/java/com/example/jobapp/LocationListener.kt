package com.example.jobapp

// LocationListener.kt

import android.location.Location
import android.os.Bundle

interface LocationListener : android.location.LocationListener {
    override fun onLocationChanged(location: Location) {
        // Handle location changes
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle location provider status changes
    }

    override fun onProviderEnabled(provider: String) {
        // Handle location provider enabled
    }

    override fun onProviderDisabled(provider: String) {
        // Handle location provider disabled
    }
}
