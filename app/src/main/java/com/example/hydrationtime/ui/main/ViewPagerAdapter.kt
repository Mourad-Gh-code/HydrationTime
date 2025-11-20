
package com.example.hydrationtime.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hydrationtime.ui.fragments.dashboard.DashboardFragment
import com.example.hydrationtime.ui.fragments.goals.GoalsFragment
import com.example.hydrationtime.ui.fragments.profile.ProfileFragment
import com.example.hydrationtime.ui.fragments.tips.TipsFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardFragment()
            1 -> GoalsFragment()
            2 -> TipsFragment()
            3 -> ProfileFragment()
            else -> DashboardFragment()
        }
    }
}