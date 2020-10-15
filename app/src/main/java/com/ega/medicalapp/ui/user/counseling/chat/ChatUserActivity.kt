package com.ega.medicalapp.ui.user.counseling.chat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.AppointmentEntity
import com.ega.medicalapp.data.model.MessagesEntity
import com.ega.medicalapp.data.model.PsychologistEntity
import com.ega.medicalapp.ui.user.counseling.CounselingViewHolder.Companion.CHAT_APPOINTMENT
import com.ega.medicalapp.ui.user.counseling.CounselingViewHolder.Companion.CHAT_PSYCHOLOGIST
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat_user.*

class ChatUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private  var appointment: AppointmentEntity = AppointmentEntity()
    private  var psychologist: PsychologistEntity = PsychologistEntity()

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user)

        if(intent != null){
            appointment = intent.getParcelableExtra(CHAT_APPOINTMENT)!!
            psychologist = intent.getParcelableExtra(CHAT_PSYCHOLOGIST)!!
        }
        else {
            finish()
        }

        rvChat.setHasFixedSize(true)
        rvChat.layoutManager = LinearLayoutManager(this)

        tvChatName.text = psychologist.name
        if(psychologist.photo!!.isNotEmpty())
            GlideApp.with(this)
                .load(
                    FirebaseStorage.getInstance().getReferenceFromUrl(psychologist.photo.toString())
                )
                .into(imgChatProfile)


        val channelID = appointment.id.toString()

        Firebase.database.reference.child("chats").child("messages").child(channelID).orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val messagesEntities: ArrayList<MessagesEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children) {
                        messagesEntities.add(childSnapshot.getValue(MessagesEntity::class.java)!!)
                    }

                    val adapter = ChatAdapter(messagesEntities)
                    adapter.notifyDataSetChanged()
                    rvChat.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@ChatUserActivity, "Load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            })

        btnSend.setOnClickListener {
            val message = etChat.text.toString()
            val timestamp = System.currentTimeMillis()
            val post = MessagesEntity(
                message,
                auth.currentUser?.uid,
                timestamp
            )

            val postValue = post.toMap()
            val childUpdate = hashMapOf<String, Any>(
                "chats/messages/$channelID/$timestamp" to postValue
            )

            database.updateChildren(childUpdate)
            database.child("chats")
                .child("channels")
                .child(channelID)
                .child("lastMessage")
                .setValue(message)
        }

        val popup = PopupMenu(this, btnMore)
        popup.menuInflater.inflate(R.menu.menu_chat, popup.menu)

        btnMore.setOnClickListener {
            popup.show()
        }

        popup.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.mEndSessionUser -> {
                    database.child("chats")
                        .child("channels")
                        .child(channelID)
                        .child("endSession")
                        .setValue(true)
                        .addOnSuccessListener {
                            database
                                .child("appointments")
                                .orderByChild("id")
                                .equalTo(channelID)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (childSnapshot in dataSnapshot.children){
                                            database
                                                .child("appointments")
                                                .child(childSnapshot.key.toString())
                                                .removeValue()
                                        }

                                        finish()
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                    }
                                })
                        }

                    true
                }
                else -> false
            }
        }
    }

}