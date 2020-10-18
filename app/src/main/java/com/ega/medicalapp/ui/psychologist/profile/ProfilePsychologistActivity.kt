package com.ega.medicalapp.ui.psychologist.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.PsychologistEntity
import com.ega.medicalapp.data.model.UserEntity
import com.ega.medicalapp.ui.user.profile.ProfileUserActivity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile_psychologist.*
import java.util.*

class ProfilePsychologistActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_psychologist)

        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        database = Firebase.database.reference

        Firebase.database.getReference("/psychologist/" + auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val psychologist = dataSnapshot.getValue(PsychologistEntity::class.java)

                    GlideApp.with(this@ProfilePsychologistActivity)
                        .load(storage.getReferenceFromUrl(psychologist?.photo.toString()))
                        .into(imgProfilePhoto)

                    imgProfilePhoto.contentDescription = psychologist?.photo.toString()
                    etName.setText(psychologist?.name)
                    etAlumni.setText(psychologist?.alumni.toString())
                    etExperience.setText(psychologist?.experience.toString())
                    etAddress.setText(psychologist?.address.toString())
                    etSipp.setText(psychologist?.sipp.toString())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@ProfilePsychologistActivity, "Load failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Logout") { dialog, id ->
                    Firebase.auth.signOut()
                    finish()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        btnUpdate.setOnClickListener {

            val user = auth.currentUser
            val photo = imgProfilePhoto.contentDescription.toString()
            val post = PsychologistEntity(
                etName.text.toString(),
                user?.email,
                photo,
                etAddress.text.toString(),
                etExperience.text.toString(),
                etAlumni.text.toString(),
                etSipp.text.toString(),
                false
            )

            val postValue = post.toMap()
            val childUpdate = hashMapOf<String, Any>(
                "psychologist/${user?.uid}" to postValue
            )

            database.updateChildren(childUpdate)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "Update success.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            baseContext, "Update failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            val profileUpdates = userProfileChangeRequest {
                displayName = etName.text.toString()
                photoUri = Uri.parse(photo)
            }

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("CHECK", "User profile updated.")
                    }
                }


        }

        btnBack.setOnClickListener {
            finish()
        }

        btnChangeProfilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, ProfileUserActivity.REQUEST_IMAGE_GET)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ProfileUserActivity.REQUEST_IMAGE_GET && resultCode == RESULT_OK && data!=null && data.data!=null) {

            val uri: Uri? = data.data
            val mimeType: String? = data.data.let { returnUri ->
                contentResolver.getType(returnUri!!)
            }
            val extension = mimeType?.substringAfterLast("/")

            val reference = "images/IMG_${UUID.randomUUID()}_$extension"
            val fileRef = storage.getReference(reference)

            fileRef.putFile(uri!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        imgProfilePhoto.contentDescription = "gs://medicalapp-e2fc9.appspot.com/$reference"
                        btnUpdate.performClick()
                    } else {
                        Toast.makeText(
                            baseContext, "Change photo failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}