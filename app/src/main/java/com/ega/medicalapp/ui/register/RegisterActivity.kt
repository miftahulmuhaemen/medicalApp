package com.ega.medicalapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.User
import com.ega.medicalapp.ui.login.LoginActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        database = Firebase.database.reference
        auth = Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btnRegister.setOnClickListener {
            auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val user = User(etName.text.toString(), currentUser?.email, "", isUser(), 0)
                        database.child("users").child(currentUser?.uid.toString()).setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext, "Registration Success.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                intent = Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    baseContext, "Registration failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            baseContext, "Registration failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        btnBackToLogin.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
            startActivity(intent)
        }
    }

    private fun isUser(): Boolean {
        return rbUser.isChecked
    }

}