package com.example.jobapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST = 1001
    private val ALLOWED_LOCATION_LATITUDE = 31.2580
    private val ALLOWED_LOCATION_LONGITUDE = 75.7069

    private var firebaseAuth: FirebaseAuth? = null
    private var slideviewpager: ViewPager? = null
    private var dots_id: LinearLayout? = null
    private lateinit var dots: Array<TextView?>
    private var viewListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                addDotsIndicator(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }
    private var login_btn: Button? = null
    private var signup_btn: Button? = null
    private var sliderAdapter: SliderAdapter? = null
    private var locationHelper: LocationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        slideviewpager = findViewById<View>(R.id.viewpager_id) as ViewPager
        dots_id = findViewById<View>(R.id.dots_id) as LinearLayout
        sliderAdapter = SliderAdapter(this)
        slideviewpager!!.adapter = sliderAdapter
        addDotsIndicator(0)
        slideviewpager!!.addOnPageChangeListener(viewListener)
        login_btn = findViewById<View>(R.id.loginBtn) as Button
        signup_btn = findViewById<View>(R.id.signup) as Button

        login_btn!!.setOnClickListener {
            val i = Intent(this@MainActivity, LoginPage::class.java)
            startActivity(i)
        }

        signup_btn!!.setOnClickListener {
            val i = Intent(this@MainActivity, Signup::class.java)
            startActivity(i)
        }

        // Initialize LocationHelper
        locationHelper = LocationHelper(this)

        // Request location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST
                )
            } else {
                // Permission already granted
                startLocationUpdates()
            }
        } else {
            // For devices running below Marshmallow, no runtime permission is needed
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        // Start receiving location updates
        locationHelper?.startLocationUpdates(LocationListenerImpl())
    }

    private fun checkuserstatus() {
        val user = firebaseAuth!!.currentUser
        if (user != null && user.isEmailVerified) {
            // User signed and go to dashboard
            startActivity(Intent(applicationContext, Dashboard::class.java))
            finish()
        } else {
            // user is not signed and go to Mainactivity
        }
    }

    override fun onStart() {
        checkuserstatus()
        super.onStart()
    }

    fun addDotsIndicator(position: Int) {
        dots = arrayOfNulls(2)
        dots_id!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(resources.getColor(R.color.colorWhite))
            dots_id!!.addView(dots[i])
        }
        if (dots.size > 0) {
            dots[position]!!.setTextColor(resources.getColor(R.color.darkblue))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates()
            }
        }
    }

    inner class LocationListenerImpl : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Handle location changes
            val latitude = location.latitude
            val longitude = location.longitude

            // Check if the app is allowed in the current location
            if (!checkLocationForGeofence(latitude, longitude)) {
                // App is not allowed in the current location
                // Show a message or restrict access
                // For simplicity, I'm just finishing the activity
                showOutOfRangeDialog()
                return
            }
            else {
                // App is allowed in the current location
                // You may add additional logic here if needed
            }
        }

        private fun showOutOfRangeDialog() {
            try {
                // Ensure the dialog is not shown multiple times
                if (!isFinishing) {
                    val builder: AlertDialog.Builder
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = AlertDialog.Builder(this@MainActivity, android.R.style.Theme_Material_Dialog_Alert)
                    } else {
                        builder = AlertDialog.Builder(this@MainActivity)
                    }
                    builder.setTitle("Out of Range")
                        .setMessage("You are out of the allowed range.")
                        .setPositiveButton(android.R.string.ok,
                            DialogInterface.OnClickListener { dialog, which -> finish() })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false) // Prevent the user from dismissing the dialog
                        .show()

                    // Log for debugging
                    Log.d("MainActivity", "Showing out of range dialog")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Exception while showing dialog: ${e.message}")
            }
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



    private fun checkLocationForGeofence(latitude: Double, longitude: Double): Boolean {
        val ALLOWED_LOCATION_RADIUS_METERS = 1000.0

        // Calculate the distance between the current location and the allowed location
        val distance = calculateDistance(
            latitude, longitude, ALLOWED_LOCATION_LATITUDE, ALLOWED_LOCATION_LONGITUDE
        )

        // Check if the distance is within the allowed radius
        return distance <= ALLOWED_LOCATION_RADIUS_METERS

    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        // Implementation of distance calculation
        // ...

        return 0.0
    }
}