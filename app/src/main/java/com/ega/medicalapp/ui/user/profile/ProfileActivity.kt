package com.ega.medicalapp.ui.user.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.UserEntity
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        database = Firebase.database.reference

        Firebase.database.getReference("/users/" + auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userEntity: UserEntity? = dataSnapshot.getValue(UserEntity::class.java)
                    GlideApp.with(this@ProfileActivity)
                        .load(storage.getReferenceFromUrl(userEntity?.photo.toString()))
                        .into(imgProfilePhoto)

                    imgProfilePhoto.contentDescription = userEntity?.photo.toString()
                    etName.setText(userEntity?.name)
                    etEmail.setText(userEntity?.email)
                    etAge.setText(userEntity?.age.toString())

                    if (userEntity?.gender!!)
                        rgGender.check(R.id.rbMale)
                    else if (!userEntity.gender!!)
                        rgGender.check(R.id.rbFemale)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity, "Registration failed.",
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
            val gender = rbMale.isChecked
            val post = UserEntity(
                etName.text.toString(),
                etEmail.text.toString(),
                imgProfilePhoto.contentDescription.toString(),
                etAge.text.toString().toInt(),
                gender
            )

            val postValue = post.toMap()
            val childUpdate = hashMapOf<String, Any>(
                "users/${auth.currentUser?.uid}" to postValue
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
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnChangeProfilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_GET)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data!=null && data.data!=null) {

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

    companion object {
        const val REQUEST_IMAGE_GET = 1
    }
}