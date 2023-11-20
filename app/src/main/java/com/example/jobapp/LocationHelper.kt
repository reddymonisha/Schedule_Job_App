package com.example.jobapp

// LocationHelper.kt

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

class LocationHelper(private val context: Context) {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationListener: LocationListener? = null

    fun startLocationUpdates(listener: LocationListener) {
        locationListener = listener

        // Check for permission if targeting Android 6.0 (API level 23) or higher
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        try {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500,   // 1-second interval
                0.5f,      // 1 meter change
                locationListener as android.location.LocationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        locationListener?.let {
            try {
                // Stop location updates
                locationManager.removeUpdates(it)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
    fun getLastKnownLocation(): Location? {
        // Implement this method to get the last known location
        // You can use LocationManager or FusedLocationProviderClient here
        // ...

        return null
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
