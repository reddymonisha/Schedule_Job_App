// GeofenceUtil.kt
package com.example.jobapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceUtil(private val context: Context) {
    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)

    // Define constants for allowed location
    companion object {
        const val ALLOWED_LOCATION_LATITUDE = 17.7114
        const val ALLOWED_LOCATION_LONGITUDE = 83.3191
        private const val TAG = "GeofenceUtil"
    }

    fun createGeofence(
        requestId: String,
        latitude: Double,
        longitude: Double,
        radius: Float,
        transitionTypes: Int
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionTypes)
            .build()
    }

    fun createGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    fun createGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun addGeofence(geofence: Geofence, pendingIntent: PendingIntent) {
        try {
            geofencingClient.addGeofences(createGeofencingRequest(geofence), pendingIntent)
                .run {
                    addOnSuccessListener {
                        Log.d(TAG, "Geofence added")
                    }
                    addOnFailureListener { e ->
                        val errorMessage = getErrorString(e)
                        Log.e(TAG, "Failed to add geofence: $errorMessage")
                    }
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
        }
    }

    fun checkLocationForGeofence(latitude: Double, longitude: Double): Boolean {
        val ALLOWED_LOCATION_RADIUS_METERS = 1000.0

        // Calculate the distance between the current location and the allowed location
        val distance = calculateDistance(
            latitude, longitude, ALLOWED_LOCATION_LATITUDE, ALLOWED_LOCATION_LONGITUDE
        )

        // Check if the distance is within the allowed radius
        return distance == ALLOWED_LOCATION_RADIUS_METERS
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        // Implementation of distance calculation
        // ...

        return 0.0
    }

    private fun getErrorString(e: Exception): String {
        if (e is ApiException) {
            return GeofenceErrorMessages.getErrorString(context, e.statusCode)
        }
        return e.localizedMessage ?: "Unknown error"
    }
}
