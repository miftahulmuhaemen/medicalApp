package com.ega.medicalapp.ui.user.counseling

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.fragment_counseling.*

class CounselingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: CounselingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_counseling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCounseling.setHasFixedSize(true)
        rvCounseling.layoutManager = LinearLayoutManager(context)

        Firebase.database.reference.child("appointments").orderByChild("patient").equalTo(auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val appointments: ArrayList<AppointmentEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children){
                        appointments.add(childSnapshot.getValue(AppointmentEntity::class.java)!!)
                    }

                    onLoadPsychologist(appointments)

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Appointments load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        svPsychologist.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    fun onLoadPsychologist(appointments: ArrayList<AppointmentEntity>){
        Firebase.database.getReference("/psychologist/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val psychologistUIDs: ArrayList<String> = ArrayList()
                    val psychologistEntity: ArrayList<PsychologistEntity> = ArrayList()
                    val psychologistAppointments: ArrayList<AppointmentEntity> = ArrayList()

                    for (childSnapshot in dataSnapshot.children){

                        val psychologistUID = childSnapshot.key.toString()
                        var isAppointmentEmpty = true

                        for(appointment in appointments){
                            if(appointment.psychologist == psychologistUID){
                                psychologistAppointments.add(appointment)
                                isAppointmentEmpty = false
                            }
                        }

                        if(isAppointmentEmpty){
                            psychologistAppointments.add(AppointmentEntity())
                        }

                        psychologistUIDs.add(psychologistUID)
                        psychologistEntity.add(childSnapshot.getValue(PsychologistEntity::class.java)!!)
                    }

                    adapter = CounselingAdapter(activity as AppCompatActivity,psychologistEntity, psychologistAppointments, psychologistUIDs)
                    adapter.notifyDataSetChanged()
                    rvCounseling.adapter = adapter

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Psychologists load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}