package com.example.jobapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Cadapter(private val list_members: ArrayList<Candidate?>, private val context: Context?,
               private val onDeleteIconClick: (String) -> Unit) :
    RecyclerView.Adapter<Cadapter.ItemViewHolder>() {

    companion object {
        const val MENU_EDIT = 101
        const val MENU_DELETE = 102
    }

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = firebaseAuth.currentUser
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? =
        firebaseDatabase.getReference("Users").child(user?.uid ?: "").child("Candidates_details")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.custommenu_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val list_items = list_members[position]

        if (list_items != null) {
            holder.name.text = list_items.name ?: ""
            holder.jobtitle.text = list_items.jobtitle
            holder.Schedule.text = "Schedule"
            holder.interview_status.text = list_items.status

            if (list_items.isInterview_status) {
                holder.interview_scheduled.text = "Interview Scheduled"
                holder.Schedule.text = "Update Schedule"
                holder.dt_layout.visibility = LinearLayout.VISIBLE
                holder.tv_date.text = list_items.intv_date
                holder.tv_time.text = list_items.intv_time
            } else {
                holder.interview_scheduled.text = "Interview not Scheduled"
                holder.dt_layout.visibility = LinearLayout.GONE
            }

            holder.Schedule.setOnClickListener {
                val i = Intent(context, Schedule::class.java)
                i.putExtra("Can_name", list_items.name)
                i.putExtra("Can_id", list_items.cid)
                i.putExtra("Can_int_date", list_items.intv_date)
                i.putExtra("Can_int_time", list_items.intv_time)
                context?.startActivity(i)
            }

            holder.deleteIcon.setOnClickListener {
                // Call onDeleteIconClick with the candidate ID
                onDeleteIconClick.invoke(list_items.cid ?: "")
            }
        }
    }

    override fun getItemCount(): Int {
        return list_members.size
    }

    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        var name: TextView = itemView.findViewById(R.id.tv_candidate_name)
        var deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
        var jobtitle: TextView = itemView.findViewById(R.id.tv_candidate_jobtitle)
        var interview_status: TextView = itemView.findViewById(R.id.tv_candidate_interview_status)
        var interview_scheduled: TextView =
            itemView.findViewById(R.id.tv_candidate_interview_scheduled)
        var tv_date: TextView = itemView.findViewById(R.id.tv_int_date_value)
        var tv_time: TextView = itemView.findViewById(R.id.tv_int_time_value)
        var Schedule: Button = itemView.findViewById(R.id.btn_schedule)
        var item_layout: LinearLayout = itemView.findViewById(R.id.customMenu)
        var dt_layout: LinearLayout = itemView.findViewById(R.id.l_intv_dt)

        init {
            item_layout.setOnCreateContextMenuListener(this)
        }

        init {
            // Add a click listener to the delete icon
            deleteIcon.setOnClickListener {
                val candidateId = list_members[adapterPosition]?.cid
                if (!candidateId.isNullOrEmpty()) {
                    onDeleteIconClick(candidateId)
                }
            }
        }

        override fun onCreateContextMenu(
            contextMenu: ContextMenu,
            view: View,
            contextMenuInfo: ContextMenuInfo
        ) {
            contextMenu.add(this.adapterPosition, MENU_EDIT, 0, "Edit")
            contextMenu.add(this.adapterPosition, MENU_DELETE, 1, "Delete")
        }
    }

    fun removeItem(position: Int) {
        val list_items = list_members[position]
        val id: String = list_items?.cid ?: ""
        databaseReference =
            firebaseDatabase.getReference("Users").child(user?.uid ?: "").child("Candidates_details")
                .child(id)
        databaseReference?.removeValue()
        notifyItemRemoved(position)
    }

    fun updateItem(position: Int) {
        val list_items = list_members[position]
        val cid: String = list_items?.cid ?: ""
        val cName: String = list_items?.name ?: ""
        val cemail: String = list_items?.email ?: ""
        val cphone: String = list_items?.phone ?: ""
        val cjobtitle: String = list_items?.jobtitle ?: ""

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.update_candidate_dialoge, null)
        dialogBuilder.setView(dialogView)

        val et_name = dialogView.findViewById<EditText>(R.id.U_input_name)
        val et_email = dialogView.findViewById<EditText>(R.id.U_input_email)
        val et_phone = dialogView.findViewById<EditText>(R.id.U_input_phone)
        val et_jobtitle = dialogView.findViewById<EditText>(R.id.U_jobTitle)
        val update_btn = dialogView.findViewById<Button>(R.id.btn_uUpdate)
        val cancle_btn = dialogView.findViewById<Button>(R.id.btn_uCancle)

        et_name.setText(cName)
        et_phone.setText(cphone)
        et_email.setText(cemail)
        et_jobtitle.setText(cjobtitle)

        val update = dialogBuilder.create()
        update.show()

        update_btn.setOnClickListener {
            val cName = et_name.text.toString()
            val cemail = et_email.text.toString()
            val cphone = et_phone.text.toString()
            val cjobtitle = et_jobtitle.text.toString()
            val cgender: String = list_items?.gender ?: ""
            val csource: String = list_items?.source ?: ""
            val cstatus: String = list_items?.status ?: ""
            val citerview: Boolean = list_items?.isInterview_status == true

            Log.i("Name", cName)

            databaseReference =
                firebaseDatabase.getReference("Users").child(user?.uid ?: "").child("Candidates_details")
                    .child(cid)
            val candidate = Candidate(
                cName,
                cemail,
                cgender,
                cjobtitle,
                cphone,
                csource,
                cstatus,
                cid,
                citerview
            )
            databaseReference?.setValue(candidate)
            update.dismiss()
        }

        cancle_btn.setOnClickListener { update.dismiss() }
    }
    fun onDeleteIconClick(candidateId: String) {
        onDeleteIconClick.invoke(candidateId)
    }
}