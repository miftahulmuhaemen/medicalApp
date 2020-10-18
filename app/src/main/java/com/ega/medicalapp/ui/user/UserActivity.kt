package com.ega.medicalapp.ui.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ega.medicalapp.R
import com.ega.medicalapp.ui.login.LoginActivity
import com.ega.medicalapp.ui.user.counseling.CounselingFragment
import com.ega.medicalapp.ui.user.emergency.EmergencyFragment
import com.ega.medicalapp.ui.user.emergency.EmergencyFragment.Companion.SMS_EXTRA
import com.ega.medicalapp.ui.user.emergency.TrustedContactFragment.Companion.CONTACT
import com.ega.medicalapp.ui.user.health.HealthFragment
import com.ega.medicalapp.ui.user.home.HomeFragment
import com.ega.medicalapp.ui.user.search.SearchFragment
import com.ega.medicalapp.util.hasPermissions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var activeFragment: Fragment

    companion object {
        const val PERMISSION_ALL = 291
    }

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
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val emergencyFragment = EmergencyFragment()
        val searchFragment = SearchFragment()
        val counselingFragment = CounselingFragment()
        val healthFragment = HealthFragment()
        val homeFragment = HomeFragment()

        activeFragment = homeFragment

        supportFragmentManager.beginTransaction().add(R.id.flUser, emergencyFragment).hide(
            emergencyFragment
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flUser, searchFragment).hide(
            searchFragment
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flUser, counselingFragment).hide(
            counselingFragment
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flUser, healthFragment).hide(
            healthFragment
        ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flUser, homeFragment).commit()

        userBottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.bnHome -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment)
                        .show(homeFragment).addToBackStack(
                            null
                        ).commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.bnHealth -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(
                        healthFragment
                    ).addToBackStack(null).commit()
                    activeFragment = healthFragment
                    true
                }
                R.id.bnCounseling -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(
                        counselingFragment
                    ).addToBackStack(null).commit()
                    activeFragment = counselingFragment
                    true
                }
                R.id.bnSearch -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(
                        searchFragment
                    ).addToBackStack(null).commit()
                    activeFragment = searchFragment
                    true
                }
                R.id.bnEmergency -> {
                    supportFragmentManager.beginTransaction().hide(activeFragment).show(
                        emergencyFragment
                    ).addToBackStack(null).commit()
                    activeFragment = emergencyFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CONTACT -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(
                        this, "You can add contact now.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            SMS_EXTRA -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(
                        this, "Message send successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "GAGAL EUH.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}