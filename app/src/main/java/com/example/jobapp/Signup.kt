package com.example.jobapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Signup : AppCompatActivity() {
    private var et_email: TextInputEditText? = null
    private var et_password: TextInputEditText? = null
    private var et_cpassword: TextInputEditText? = null
    private var btn_signup: Button? = null
    private var progressDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Action Bar and its Title
        val actionBar = supportActionBar
        actionBar?.apply {
            title = "Create Account"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.back_icon)
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        et_cpassword = findViewById(R.id.et_cpassword)
        btn_signup = findViewById(R.id.btn_signup)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Sign Up...")

        btn_signup?.setOnClickListener {
            val email = et_email?.text?.toString()?.trim() ?: ""
            val password = et_password?.text?.toString()?.trim() ?: ""
            val cpassword = et_cpassword?.text?.toString()?.trim() ?: ""

            if (password != cpassword) {
                et_cpassword?.setError("Password is not Matching")
                et_cpassword?.requestFocus()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                et_email?.setError("Invalid Email")
                et_email?.requestFocus()
            } else if (password.length < 4 || password.length > 10) {
                et_password?.setError("Between 4 to 10 alphanumeric Characters")
                et_password?.requestFocus()
            } else {
                registerUser(email, password)
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        progressDialog?.show()

        mAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                progressDialog?.dismiss()
                if (task.isSuccessful) {
                    val user = mAuth?.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    this@Signup,
                                    "Verification mail sent to your Email",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(applicationContext, LoginPage::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@Signup,
                                    "" + verificationTask.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this@Signup, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            ?.addOnFailureListener { e ->
                progressDialog?.dismiss()
                Toast.makeText(this@Signup, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun login_page_open(view: View?) {
        val intent = Intent(applicationContext, LoginPage::class.java)
        startActivity(intent)
        finish()
    }
}
