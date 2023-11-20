package com.example.jobapp

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class add_candidate : AppCompatActivity() {
    var et_name: EditText? = null
    var et_email: EditText? = null
    var et_phone: EditText? = null
    var et_status: EditText? = null
    var et_source: EditText? = null
    var et_jobtitle: EditText? = null
    var rdm: RadioButton? = null
    var rdf: RadioButton? = null
    var bt_add_candidate: Button? = null
    var firebaseAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_candidate)

        // Action Bar and its Title
        val actionBar = supportActionBar
        actionBar?.setTitle("Add Candidate")
        // enable back button
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.back_icon)
        et_name = findViewById(R.id.Can_input_name)
        et_email = findViewById(R.id.Can_input_email)
        et_jobtitle = findViewById(R.id.Can_input_jobTitle)
        et_phone = findViewById(R.id.Can_input_phone)
        et_status = findViewById(R.id.Can_input_currentStatus)
        et_source = findViewById(R.id.Can_input_source)
        rdm = findViewById(R.id.Can_rdm)
        rdf = findViewById(R.id.Can_rdf)
        bt_add_candidate = findViewById(R.id.addCandidateBtn)
        bt_add_candidate?.setOnClickListener(View.OnClickListener {
            val name = et_name?.text?.toString() ?: ""
            val email = et_email?.text?.toString() ?: ""
            val jobtitle = et_jobtitle?.text?.toString() ?: ""
            val phone = et_phone?.text?.toString() ?: ""
            val status = et_status?.text?.toString() ?: ""
            val source = et_source?.text?.toString() ?: ""
            var gender = ""

            if (rdm?.isChecked == true) {
                gender = rdm!!.text.toString()
            } else if (rdf?.isChecked == true) {
                gender = rdf!!.text.toString()
            } else {
                rdm?.setError("Select Gender")
                // Toast.makeText(add_candidate.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(name)) {
                et_name?.setError("Please fill the Name")
                et_name?.setFocusable(true)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                et_email?.setError("Invalid Email")
                et_email?.setFocusable(true)
            } else if (TextUtils.isEmpty(jobtitle)) {
                et_jobtitle?.setError("Please fill the Job title")
                et_jobtitle?.setFocusable(true)
            } else if (TextUtils.isEmpty(phone)) {
                et_phone?.setError("Please fill the Mobile Number")
                et_phone?.setFocusable(true)
            } else if (TextUtils.isEmpty(status)) {
                et_status?.setError("Please fill the Status")
                et_status?.setFocusable(true)
            } else if (TextUtils.isEmpty(source)) {
                et_source?.setError("Please fill the Source")
                et_source?.setFocusable(true)
            } else {
                Addcandidate(name, email, jobtitle, phone, status, source, gender)
            }
        })

    }

    private fun Addcandidate(
        name: String,
        email: String,
        jobtitle: String,
        phone: String,
        status: String,
        source: String,
        gender: String
    ) {
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference =
            firebaseDatabase!!.getReference("Users").child(user!!.uid).child("Candidates_details")
        val id = databaseReference!!.push().key
        val scheduled = false
        val candidate =
            Candidate(name, email, gender, jobtitle, phone, source, status, id, scheduled)
        databaseReference!!.child(id!!).setValue(candidate)
        et_name!!.setText("")
        et_email!!.setText("")
        et_jobtitle!!.setText("")
        et_phone!!.setText("")
        et_status!!.setText("")
        et_source!!.setText("")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}