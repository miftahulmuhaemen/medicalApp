package com.ega.medicalapp.ui.user.emergency

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.EmergencyEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var emergencies: ArrayList<EmergencyEntity> = ArrayList()

    companion object {
        const val SMS_EXTRA = 922
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_emergency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserUID = auth.currentUser?.uid.toString()

        Firebase.database.reference
            .child("emergency")
            .child(currentUserUID)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        emergencies.add(childSnapshot.getValue(EmergencyEntity::class.java)!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        activity, "Load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        btnHelp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                type = "vnd.android-dir/mms-sms"

                var numbers = ""
                for(emergency in emergencies){
                    numbers += "${emergency.phone.toString()};"
                }

                data = Uri.parse("smsto:$numbers")
                putExtra("sms_body", "H E L E P!")
                putExtra("exit_on_sent", true)

            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, SMS_EXTRA)
            }
        }

        btnSetContact.setOnClickListener {
           activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.flUser, TrustedContactFragment())?.addToBackStack("TAG")?.commit()
        }
    }
}