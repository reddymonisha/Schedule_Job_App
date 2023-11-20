package com.example.jobapp

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Arrays
import java.util.Calendar

class Schedule : AppCompatActivity() {
    var et_can_name: TextInputEditText? = null
    var btn_update_schedule: Button? = null
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0
    var firebaseAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)


        // Action Bar and its Title
        val actionBar = supportActionBar
        actionBar?.setTitle("Schedule")
        Log.i("Year and month", "$year and $month")
        // enable back button
        //  actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setDisplayShowHomeEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        com.example.jobapp.Schedule.Companion.date =
            findViewById<View>(R.id.selectdate) as Button
        com.example.jobapp.Schedule.Companion.time =
            findViewById<View>(R.id.selecttime) as Button
        com.example.jobapp.Schedule.Companion.set_date =
            findViewById<View>(R.id.set_date) as TextView
        com.example.jobapp.Schedule.Companion.set_time =
            findViewById<View>(R.id.set_time) as TextView
        et_can_name = findViewById(R.id.schedule_can_name)
        val i = intent
        //        et_can_name.setText(i.getStringExtra("Can_name"));
        com.example.jobapp.Schedule.Companion.date?.setOnClickListener(View.OnClickListener { // Show Date dialog
            showDialog(com.example.jobapp.Schedule.Companion.Date_id)
        })
        com.example.jobapp.Schedule.Companion.time?.setOnClickListener(View.OnClickListener { // Show time dialog
            showDialog(com.example.jobapp.Schedule.Companion.Time_id)
        })

        // Can info update and fetch
        val cid = i.getStringExtra("Can_id")
        Log.i("Can id : ", cid!!)
        val cname = i.getStringExtra("Can_name")
        if (!TextUtils.isEmpty(cname)) {
            et_can_name?.setText(cname)
        }
        val intv_date = i.getStringExtra("Can_int_date")
        val intv_time = i.getStringExtra("Can_int_time")
        Log.i("Date : ", intv_date!!)
        Log.i(" Time : ", intv_time!!)
        if (!TextUtils.isEmpty(intv_date)) {
            val date = Arrays.asList(*intv_date.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
            Log.i("Test : ", date[0])
            month = Integer.valueOf(date[0])
            day = Integer.valueOf(date[1])
            year = Integer.valueOf(date[2])
            val date1 = "$month/$day/$year"
            com.example.jobapp.Schedule.Companion.set_date?.setText(date1)
        }
        if (!TextUtils.isEmpty(intv_time)) {
            val date = Arrays.asList(*intv_time.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
            hour = Integer.valueOf(date[0])
            minute = Integer.valueOf(date[1])
            val time1 = "$hour:$minute"
            com.example.jobapp.Schedule.Companion.set_time?.setText(time1)
        }
        Log.i(
            "Set time : ",
            com.example.jobapp.Schedule.Companion.set_time?.getText().toString()
        )
        Log.i(
            " Set date",
            com.example.jobapp.Schedule.Companion.set_date?.getText().toString()
        )
        btn_update_schedule = findViewById(R.id.btn_schedule_update)
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference =
            firebaseDatabase!!.getReference("Users").child(user!!.uid).child("Candidates_details")
                .child(cid)
        btn_update_schedule!!.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(
                    com.example.jobapp.Schedule.Companion.set_date?.getText().toString()
                ) || TextUtils.isEmpty(
                    com.example.jobapp.Schedule.Companion.set_time?.getText().toString()
                )
            ) {
                Toast.makeText(this@Schedule, "Select the Date and Time", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(
                    "testing",
                    com.example.jobapp.Schedule.Companion.set_date?.getText().toString()
                )
                databaseReference!!.child("intv_date").setValue(
                    com.example.jobapp.Schedule.Companion.set_date?.getText().toString()
                )
                databaseReference!!.child("intv_time").setValue(
                    com.example.jobapp.Schedule.Companion.set_time?.getText().toString()
                )
                databaseReference!!.child("interview_status").setValue(true)
                Toast.makeText(this@Schedule, "Interview Schedule Updated", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(applicationContext, Candidate_view::class.java))
                finish()
            }
        })
    }

    override fun onCreateDialog(id: Int): Dialog? {

        // Get the calander
        val c = Calendar.getInstance()

        // From calander get the year, month, day, hour, minute
        if (day == 0 && hour == 0) {
            year = c[Calendar.YEAR]
            month = c[Calendar.MONTH]
            day = c[Calendar.DAY_OF_MONTH]
            hour = c[Calendar.HOUR_OF_DAY]
            minute = c[Calendar.MINUTE]
        }
        when (id) {
            com.example.jobapp.Schedule.Companion.Date_id ->
                // Open the datepicker dialog
                return DatePickerDialog(
                    this@Schedule, date_listener, year,
                    month, day
                )

            com.example.jobapp.Schedule.Companion.Time_id ->
                // Open the timepicker dialog
                return TimePickerDialog(
                    this@Schedule, time_listener, hour,
                    minute, false
                )
        }
        return null
    }

    // Date picker dialog
    var date_listener =
        OnDateSetListener { view, year, month, day -> // store the data in one string and set it to text
            val date1 = "$month/$day/$year"
            com.example.jobapp.Schedule.Companion.set_date?.setText(date1)
        }
    var time_listener =
        OnTimeSetListener { view, hour, minute -> // store the data in one string and set it to text
            val time1 = "$hour:$minute"
            com.example.jobapp.Schedule.Companion.set_time?.setText(time1)
        }

    companion object {
        private var date: Button? = null
        private var time: Button? = null
        private var set_date: TextView? = null
        private var set_time: TextView? = null
        private const val Date_id = 0
        private const val Time_id = 1
    }
}