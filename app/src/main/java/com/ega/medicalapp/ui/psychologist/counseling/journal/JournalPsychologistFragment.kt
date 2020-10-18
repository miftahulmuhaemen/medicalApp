package com.ega.medicalapp.ui.psychologist.counseling.journal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.data.model.JournalEntity
import com.ega.medicalapp.ui.psychologist.progress.ProgressViewHolder
import com.ega.medicalapp.ui.user.journal.JournalAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_journal_psychologist.*
import java.util.ArrayList

class JournalPsychologistFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_psychologist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        database = Firebase.database.reference

        val channel: ChannelEntity = arguments?.getParcelable(ProgressViewHolder.CHAT_CHANNEL)!!
        val userUID = channel.patient.toString()

        rvJournal.setHasFixedSize(true)
        rvJournal.layoutManager = LinearLayoutManager(activity)

        Firebase.database.getReference("/journals/$userUID" )
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val journalEntities: ArrayList<JournalEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children){
                        journalEntities.add(childSnapshot.getValue(JournalEntity::class.java)!!)
                    }

                    val adapter = JournalAdapter(journalEntities)
                    adapter.notifyDataSetChanged()
                    rvJournal.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Loading failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}