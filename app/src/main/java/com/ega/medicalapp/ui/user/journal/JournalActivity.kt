package com.ega.medicalapp.ui.user.journal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.JournalEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_journal.*
import java.text.SimpleDateFormat
import java.util.*

class JournalActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        auth = Firebase.auth
        database = Firebase.database.reference

        rvJournal.setHasFixedSize(true)
        rvJournal.layoutManager = LinearLayoutManager(this)

        Firebase.database.getReference("/journals/" + auth.currentUser?.uid)
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
                        this@JournalActivity, "Loading failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })


        btnSave.setOnClickListener {

            val pattern = "yyyy-MM-dd"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
            val date: String = simpleDateFormat.format(Date())

            val post = JournalEntity(
                date,
                etJournal.text.toString()
            )

            val postValue = post.toMap()
            val childUpdate = hashMapOf<String, Any>(
                "journals/${auth.currentUser?.uid}/$date" to postValue
            )

            database.updateChildren(childUpdate)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "Journal update success.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            baseContext, "Journal update failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        btnBack.setOnClickListener{
            finish()
        }

    }
}