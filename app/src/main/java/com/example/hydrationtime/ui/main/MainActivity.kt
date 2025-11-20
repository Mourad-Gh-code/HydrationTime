package com.example.hydrationtime.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.ActivityMainBinding

/**
 * MainActivity - Main activity with bottom navigation
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Disable swipe
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.nav_statistics -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.nav_tips -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                R.id.nav_settings -> {
                    binding.viewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }

        // Sync ViewPager with BottomNavigation
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }
}
