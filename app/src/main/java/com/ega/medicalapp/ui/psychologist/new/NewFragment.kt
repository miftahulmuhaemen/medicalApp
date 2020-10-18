package com.ega.medicalapp.ui.psychologist.new

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.AppointmentEntity
import com.ega.medicalapp.data.model.PsychologistEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_new.*

class NewFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: NewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserUID = auth.currentUser?.uid.toString()
        val database = Firebase.database.reference

        rvPatientRequest.setHasFixedSize(true)
        rvPatientRequest.layoutManager = LinearLayoutManager(context)

        database.child("psychologist/${auth.currentUser?.uid.toString()}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val psychologist: PsychologistEntity? =
                        dataSnapshot.getValue(PsychologistEntity::class.java)
                    swOnline.isChecked = psychologist?.online!!
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Appointments load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        database.child("appointments").orderByChild("psychologist").equalTo(auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val appointments: ArrayList<AppointmentEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children) {
                        val appointment = childSnapshot.getValue(AppointmentEntity::class.java)!!
                        if (resources.getString(R.string.waiting) == appointment.status)
                            appointments.add(appointment)
                    }

                    adapter = NewAdapter(appointments)
                    adapter.notifyDataSetChanged()
                    rvPatientRequest.adapter = adapter

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Appointments load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        swOnline.setOnCheckedChangeListener { _, check ->
            database
                .child("psychologist")
                .child(currentUserUID)
                .child("online")
                .setValue(check)
                .addOnSuccessListener {
                    Toast.makeText(
                        activity, "Status changed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}