package com.ega.medicalapp.ui.psychologist

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.PsychologistEntity
import com.ega.medicalapp.ui.login.LoginActivity
import com.ega.medicalapp.ui.psychologist.new.NewFragment
import com.ega.medicalapp.ui.psychologist.profile.ProfilePsychologistActivity
import com.ega.medicalapp.ui.psychologist.history.HistoryFragment
import com.ega.medicalapp.ui.psychologist.progress.InProgressFragment
import com.ega.medicalapp.ui.user.UserActivity
import com.ega.medicalapp.util.hasPermissions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_psychologist.*
import kotlinx.android.synthetic.main.activity_psychologist.tvName

class PsychologistActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var activeFragment: Fragment

    override fun onStart() {
        super.onStart()

        auth = Firebase.auth
        if (auth.currentUser == null) {
            intent = Intent(
                this,
                LoginActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions, UserActivity.PERMISSION_ALL);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psychologist)

        val newFragment = NewFragment()
        val inProgressFragment = InProgressFragment()
        val historyFragment = HistoryFragment()

        activeFragment = newFragment

        supportFragmentManager.beginTransaction().add(R.id.flPsychologist, historyFragment).hide(
            historyFragment
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flPsychologist, inProgressFragment).hide(
            inProgressFragment
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flPsychologist, newFragment).commit()

        psychologistBottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.bnNew -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(newFragment).addToBackStack(
                            null
                        ).commit()
                    activeFragment = newFragment
                    true
                }
                R.id.bnInProgress -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(
                        inProgressFragment
                    ).addToBackStack(null).commit()
                    activeFragment = inProgressFragment
                    true
                }
                R.id.bnHistory -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(
                        historyFragment
                    ).addToBackStack(null).commit()
                    activeFragment = historyFragment
                    true
                }
                else -> false
            }
        }

        val popup = PopupMenu(this, btnMore)
        popup.menuInflater.inflate(R.menu.menu_psychologist_activity, popup.menu)

        btnMore.setOnClickListener {
            popup.show()
        }

        popup.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.mEditProfile -> {
                    val intent = Intent(this, ProfilePsychologistActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.mLogout -> {
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
                    true
                }
                else -> false
            }
        }

        Firebase.database.getReference("/psychologist/" + Firebase.auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val psychologistEntity = dataSnapshot.getValue(PsychologistEntity::class.java)
                    val name = psychologistEntity?.name
                    tvName.text = name
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

}