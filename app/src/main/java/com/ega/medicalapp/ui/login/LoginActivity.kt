package com.ega.medicalapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.User
import com.ega.medicalapp.ui.register.RegisterActivity
import com.ega.medicalapp.ui.psychologist.PsychologistActivity
import com.ega.medicalapp.ui.user.UserActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etEmail
import kotlinx.android.synthetic.main.activity_login.etPassword

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        database = Firebase.database.reference
        definingUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnCreateAccount.setOnClickListener {
            intent = Intent(this, RegisterActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        definingUser()
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun definingUser(){

        val currentUser = auth.currentUser
        if(currentUser != null){

            database = Firebase.database.getReference("/users/" + currentUser.uid )

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.getValue(User::class.java)
                    if(user?.isUser!!){
                        intent = Intent(this@LoginActivity, UserActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                        startActivity(intent)
                    } else {
                        intent = Intent(this@LoginActivity, PsychologistActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                        startActivity(intent)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }

            database.addListenerForSingleValueEvent(postListener)
        }

    }
}