package com.ega.medicalapp.ui.user.health.meditation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.MeditationEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_health_meditation.*

class MeditationFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_health_meditation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMeditation.setHasFixedSize(true)
        rvMeditation.layoutManager = LinearLayoutManager(context)

        Firebase.database.getReference("/meditations/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val meditationEntity: ArrayList<MeditationEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children){
                        meditationEntity.add(childSnapshot.getValue(MeditationEntity::class.java)!!)
                    }

                    val adapter = MeditationAdapter(meditationEntity, activity as AppCompatActivity)
                    adapter.notifyDataSetChanged()
                    rvMeditation.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Meditation load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}