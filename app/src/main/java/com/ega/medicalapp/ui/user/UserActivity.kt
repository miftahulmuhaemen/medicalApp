package com.ega.medicalapp.ui.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ega.medicalapp.R
import com.ega.medicalapp.ui.login.LoginActivity
import com.ega.medicalapp.ui.register.RegisterActivity
import com.ega.medicalapp.ui.user.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user.view.*

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        if(auth.currentUser == null){
            intent = Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val userPagerAdapter = UserPager(this)
        userViewPager.adapter = userPagerAdapter

        userViewPager.isUserInputEnabled = false
        userBottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.bnHome -> {
                    userViewPager.currentItem = 0
                    true
                }
                R.id.bnHealth -> {
                    userViewPager.currentItem = 1
                    true
                }
                R.id.bnCounseling -> {
                    userViewPager.currentItem = 2
                    true
                }
                R.id.bnSearch -> {
                    userViewPager.currentItem = 3
                    true
                }
                R.id.bnEmergency -> {
                    userViewPager.currentItem = 4
                    true
                }
                else -> false
            }
        }
    }

}