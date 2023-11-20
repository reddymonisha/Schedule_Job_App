package com.example.jobapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Candidate_view : AppCompatActivity() {
    var menuRecyclerView: RecyclerView? = null
    var cadapter: Cadapter? = null
    var candidates = ArrayList<Candidate?>()
    var firebaseAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate_view)
        menuRecyclerView = findViewById(R.id.candidate_itemview)
        val manager = LinearLayoutManager(this@Candidate_view)
        menuRecyclerView?.setLayoutManager(manager)
        // menuRecyclerView.setAdapter(menuAdapter);
        registerForContextMenu(menuRecyclerView)
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference =
            firebaseDatabase!!.getReference("Users").child(user!!.uid).child("Candidates_details")
        val floatingActionButton =
            findViewById<View>(R.id.floating_action_button) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            startActivity(Intent(applicationContext, add_candidate::class.java))
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                candidates.clear()
                for (ds in dataSnapshot.children) {
                    val item = ds.getValue(Candidate::class.java)
                    candidates.add(item)
                }
                cadapter = Cadapter(candidates, this@Candidate_view){
                        candidateId ->
                }
                menuRecyclerView!!.setAdapter(cadapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            101 -> {
                cadapter?.updateItem(item.groupId)
                return true
            }

            102 -> {
                cadapter?.onDeleteIconClick(candidates[item.groupId]?.cid ?: "")
                return true
            }
        }
        return false
    }
}