package com.example.jobapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginPage : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var et_email: TextInputEditText? = null
    private var et_password: TextInputEditText? = null
    private var tv_forgot_password: TextView? = null
    private var btn_loginup: Button? = null
    private var mgoogleloginbtn: SignInButton? = null
    private var progressDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        setupActionBar()
        setupGoogleSignIn()
        setupViews()

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance()
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.apply {
            title = "Login"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.back_icon)
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupViews() {
        et_email = findViewById(R.id.let_email)
        et_password = findViewById(R.id.let_password)
        tv_forgot_password = findViewById(R.id.tvforgot_password)
        btn_loginup = findViewById(R.id.loginBtn)
        progressDialog = ProgressDialog(this)

        tv_forgot_password?.setOnClickListener { showRecoverPasswordDialogue() }

        btn_loginup?.setOnClickListener {
            val email = et_email?.text.toString().trim()
            val password = et_password?.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                et_email?.setError("Invalid Email")
                et_email?.isFocusable = true
            } else {
                loginuser(email, password)
            }
        }

        mgoogleloginbtn = findViewById(R.id.googleLoginBtn)
        mgoogleloginbtn?.setOnClickListener {
            val signInIntent = mGoogleSignInClient?.signInIntent
            signInIntent?.let {
                startActivityForResult(it, RC_SIGN_IN)
            } ?: run {
                // Handle the case where signInIntent is null
                Toast.makeText(this@LoginPage, "Error signing in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecoverPasswordDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recover Password")

        val linearLayout = LinearLayout(this)
        val emailEt = EditText(this).apply {
            hint = "Email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            minEms = 16
        }
        linearLayout.addView(emailEt)
        linearLayout.setPadding(10, 10, 10, 10)
        builder.setView(linearLayout)

        builder.setPositiveButton("Recover") { _, _ ->
            val email = emailEt.text.toString().trim()
            beginRecovery(email)
        }

        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.create().show()
    }

    private fun beginRecovery(email: String) {
        progressDialog?.apply {
            setMessage("Sending Email...")
            show()
        }

        mAuth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener { task ->
                progressDialog?.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginPage, "Email Sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginPage, "Failed...", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { e ->
                progressDialog?.dismiss()
                Toast.makeText(this@LoginPage, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginuser(email: String, password: String) {
        progressDialog?.apply {
            setMessage("Log in...")
            show()
        }

        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                progressDialog?.dismiss()

                if (task.isSuccessful) {
                    val user = mAuth?.currentUser

                    // Check if the user is signing in for the first time
                    if (task.result?.additionalUserInfo?.isNewUser == true) {
                        // Store user info in the Firebase database
                        val uid = user?.uid
                        val email = user?.email
                        uid?.let {
                            val hashMap = hashMapOf(
                                "Email" to email,
                                "Name" to "",
                                "Mobile_number" to "",
                                "Gender" to "",
                                "Job_title" to "",
                                "Uid" to uid
                            )

                            val database = FirebaseDatabase.getInstance()
                            val reference = database.getReference("Users")
                            reference.child(uid).child("User_Info").setValue(hashMap)
                        }
                    }

                    // Check if the user's email is verified
                    if (user?.isEmailVerified == true) {
                        Toast.makeText(
                            this@LoginPage,
                            "Login Successful ${user.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(applicationContext, Dashboard::class.java))
                        finish()
                    } else {
                        // If email is not verified, prompt the user to verify it
                        Toast.makeText(
                            this@LoginPage,
                            "Please Verify your Email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // If the authentication fails, show an error message
                    Toast.makeText(
                        this@LoginPage,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            ?.addOnFailureListener { e ->
                progressDialog?.dismiss()
                Toast.makeText(this@LoginPage, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this@LoginPage, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth?.currentUser
                    if (task.result?.additionalUserInfo?.isNewUser == true) {
                        // Store user info in the Firebase database
                        val uid = user?.uid
                        val email = user?.email
                        val hashMap = HashMap<String, String?>()
                        hashMap["Email"] = email
                        hashMap["Name"] = ""
                        hashMap["Mobile_number"] = ""
                        hashMap["Gender"] = ""
                        hashMap["Job_title"] = ""
                        hashMap["Uid"] = uid
                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("Users")
                        reference.child(uid!!).child("User_Info").setValue(hashMap)
                    }

                    Toast.makeText(
                        this@LoginPage,
                        "Login Successful ${user?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(applicationContext, Dashboard::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginPage, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this@LoginPage, "" + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}
