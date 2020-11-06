package com.ega.medicalapp.ui.user.emergency

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.EmergencyEntity
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_emergency_list.*


class TrustedContactFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    companion object {
        const val CONTACT = 2123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        Contacts.initialize(activity);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_emergency_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvContacts.setHasFixedSize(true)
        rvContacts.layoutManager = LinearLayoutManager(context)

        val currentUserUID = auth.currentUser?.uid

        Firebase.database.getReference("/emergency/$currentUserUID")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val emergencies: ArrayList<EmergencyEntity> = ArrayList()
                    for (childSnapshot in dataSnapshot.children) {
                        emergencies.add(childSnapshot.getValue(EmergencyEntity::class.java)!!)
                    }

                    val adapter = TrustedContactAdapter(emergencies)
                    adapter.notifyDataSetChanged()
                    rvContacts.adapter = adapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Contacts load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        btnAddTrustedContact.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CONTACT)
            } else {
                val contacts: List<Contact> = Contacts.getQuery().find()
                var name = emptyArray<String>()
                var phoneNumber = emptyArray<String>()

                val timestamp = System.currentTimeMillis()

                for (contact in contacts) {
                    if (!contact.givenName.isNullOrEmpty()) {
                        if (!contact.phoneNumbers.isNullOrEmpty())
                            if (!contact.phoneNumbers.first().number.isNullOrEmpty()) {
                                name += contact.givenName
                                phoneNumber += contact.phoneNumbers.first().number
                            }
                    }
                }

                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Choose contact")
                    .setItems(name) { _, position ->

                        val post = EmergencyEntity(
                            name[position],
                            phoneNumber[position]
                        )

                        val postValue = post.toMap()
                        val childUpdate = hashMapOf<String, Any>(
                            "emergency/$currentUserUID/$timestamp" to postValue
                        )

                        Firebase.database.reference.updateChildren(childUpdate)
                    }
                    .show()
            }
        }
    }

}