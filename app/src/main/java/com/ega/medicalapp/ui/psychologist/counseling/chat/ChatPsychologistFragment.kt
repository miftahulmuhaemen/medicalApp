package com.ega.medicalapp.ui.psychologist.counseling.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.data.model.MessagesEntity
import com.ega.medicalapp.ui.psychologist.history.HistoryViewHolder.Companion.CHAT_HISTORY
import com.ega.medicalapp.ui.psychologist.progress.ProgressViewHolder.Companion.CHAT_CHANNEL
import com.ega.medicalapp.ui.user.counseling.chat.ChatAdapter
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat_psychologist.*

class ChatPsychologistFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_psychologist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        database = Firebase.database.reference

        val channel: ChannelEntity = arguments?.getParcelable(CHAT_CHANNEL)!!
        val isHistory: Boolean? = arguments?.getBoolean(CHAT_HISTORY)
        val channelID = channel.id.toString()

        rvChat.setHasFixedSize(true)
        rvChat.layoutManager = LinearLayoutManager(activity)

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
                        activity, "Load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        btnSend.setOnClickListener {
            Log.d("CHECK", "sended")
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
                .addOnSuccessListener {

                    Log.d("CHECK", "messages")
                    database.child("chats")
                        .child("channels")
                        .child(channelID)
                        .child("lastMessage")
                        .setValue(message)
                }
        }

        if(isHistory!!){
            etChat.isEnabled = false
            btnSend.isEnabled = false
        }

    }
}