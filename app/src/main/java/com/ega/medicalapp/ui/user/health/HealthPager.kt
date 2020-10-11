package com.ega.medicalapp.ui.user.health

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ega.medicalapp.ui.user.counseling.CounselingFragment
import com.ega.medicalapp.ui.user.emergency.EmergencyFragment
import com.ega.medicalapp.ui.user.health.HealthFragment
import com.ega.medicalapp.ui.user.health.article.ArticleFragment
import com.ega.medicalapp.ui.user.health.meditation.MeditationFragment
import com.ega.medicalapp.ui.user.home.HomeFragment
import com.ega.medicalapp.ui.user.search.SearchFragment

 class HealthPager(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ArticleFragment()
            1 -> MeditationFragment()
            else -> Fragment()
        }
    }

}