package com.ega.medicalapp.ui.user.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ServiceEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.FirebaseStorage.getInstance
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvService.setHasFixedSize(true)
        rvService.layoutManager = LinearLayoutManager(activity)

        spLocation.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
            val locations = resources.getStringArray(R.array.locationSample)
            updateServices(locations[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateServices(location: String){
        Firebase.database.getReference("services")
            .orderByChild("location")
            .equalTo(location)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val service: ArrayList<ServiceEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children) {
                        service.add(childSnapshot.getValue(ServiceEntity::class.java)!!)
                    }

                    val adapter = ServiceAdapter(service)
                    adapter.notifyDataSetChanged()
                    rvService.adapter = adapter
                    labelSelect.visibility = View.GONE

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
