package com.example.jobapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class Dashboard : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun checkuserstatus() {
        val user = firebaseAuth!!.currentUser
        if (user != null && user.isEmailVerified) {
            // User signed and stay here
        } else {
            // user is not signed and go to Mainactivity
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        checkuserstatus()
        super.onStart()
    }

    // inflate the option Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handle the menu item clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_logout) {
            firebaseAuth!!.signOut()
            checkuserstatus()
        }
        return super.onOptionsItemSelected(item)
    }

    fun profile_page_open(view: View?) {
        startActivity(Intent(applicationContext, ProfilePage::class.java))
    }

    fun add_candidate_page_open(view: View?) {
        startActivity(Intent(applicationContext, add_candidate::class.java))
    }

    fun candidate_view_page_open(view: View?) {
        startActivity(Intent(applicationContext, Candidate_view::class.java))
    }

    fun schedule_view_page_open(view: View?) {
        startActivity(Intent(applicationContext, Schedule::class.java))
    }
}