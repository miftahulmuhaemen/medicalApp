package com.ega.medicalapp.ui.psychologist.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.ui.psychologist.progress.ProgressAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_in_progress.*

class HistoryFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserUID = auth.currentUser?.uid.toString()
        val database = Firebase.database.reference

        rvHistory.setHasFixedSize(true)
        rvHistory.layoutManager = LinearLayoutManager(context)

        database.child("chats")
            .child("channels")
            .orderByChild("psychologist").equalTo(currentUserUID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val channels: ArrayList<ChannelEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children) {
                        val channel = childSnapshot.getValue(ChannelEntity::class.java)!!
                        if (channel.endSession != false)
                            channels.add(channel)
                    }

                    val adapter = HistoryAdapter(channels)
                    adapter.notifyDataSetChanged()
                    rvHistory.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Appointments load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}