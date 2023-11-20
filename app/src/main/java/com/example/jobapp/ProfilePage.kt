package com.example.jobapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.squareup.picasso.Picasso
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner

class ProfilePage : AppCompatActivity() {
    var genderSpinner: MaterialBetterSpinner? = null
    var jobTitleSpinner: MaterialBetterSpinner? = null
    var firebaseAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    var rdm: RadioButton? = null
    var rdf: RadioButton? = null
    var rdo: RadioButton? = null
    var btn_profile_update: Button? = null
    var et_name: TextInputEditText? = null
    var et_email: TextInputEditText? = null
    var et_phone: TextInputEditText? = null
    var et_jobtitle: TextInputEditText? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        // Action Bar and its Title
        val actionBar = supportActionBar
        actionBar?.setTitle("My Profile")
        // enable back button
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.back_icon)
        progressDialog = ProgressDialog(this)


        // init database
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference =
            firebaseDatabase!!.getReference("Users").child(user!!.uid).child("User_Info")

        // init views
        et_jobtitle = findViewById(R.id.input_job_Title)
        et_name = findViewById(R.id.input_name)
        et_email = findViewById(R.id.input_email)
        et_phone = findViewById(R.id.input_phone)
        rdm = findViewById(R.id.input_male)
        rdf = findViewById(R.id.input_female)
        btn_profile_update = findViewById(R.id.btn_profile_update)
        btn_profile_update?.setOnClickListener(View.OnClickListener {
            progressDialog!!.setMessage("Updating Profile")
            progressDialog!!.show()
            val Email = et_email?.getText().toString()
            val Phone = et_phone?.getText().toString()
            val Name = et_name?.getText().toString()
            val Jobtitle = et_jobtitle?.getText().toString()
            val Gender: String
            Gender = if (rdm!!.isChecked()) {
                rdm?.getText().toString()
            } else {
                rdf?.getText().toString()
            }
            val result = HashMap<String, Any>()
            result["Email"] = Email
            result["Name"] = Name
            result["Mobile_number"] = Phone
            result["Gender"] = Gender
            result["Job_title"] = Jobtitle
            databaseReference!!.updateChildren(result)
                .addOnSuccessListener {
                    progressDialog!!.dismiss()
                    Toast.makeText(this@ProfilePage, "Profile Updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, Dashboard::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@ProfilePage,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        })
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val Email = dataSnapshot.child("Email").value.toString()
                val Phone = dataSnapshot.child("Mobile_number").value.toString()
                val Name = dataSnapshot.child("Name").value.toString()
                val Jobtitle = dataSnapshot.child("Job_title").value.toString()
                val Gender = dataSnapshot.child("Gender").value.toString()
                et_email?.setText(Email)
                et_name?.setText(Name)
                et_phone?.setText(Phone)
                et_jobtitle?.setText(Jobtitle)
                if (TextUtils.equals(Gender, rdm?.getText().toString())) {
                    rdm!!.setChecked(true)
                } else if (TextUtils.equals(Gender, rdf?.getText().toString())) {
                    rdf?.setChecked(true)
                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}