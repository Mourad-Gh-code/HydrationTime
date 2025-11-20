package com.example.hydrationtime.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator


/**
 * MainActivity - Activité principale avec 4 onglets
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val tabTitles = listOf("Tableau de bord", "Objectifs", "Conseils", "Mon compte")
    private val tabIcons = listOf(
        R.drawable.ic_dashboard,
        R.drawable.ic_goals,
        R.drawable.ic_tips,
        R.drawable.ic_profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Désactiver le swipe si nécessaire
        binding.viewPager.isUserInputEnabled = true

        // Lier TabLayout avec ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.setIcon(tabIcons[position])
        }.attach()
    }
}
