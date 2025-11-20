package com.example.hydrationtime.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.ActivityOnboardingBinding
import com.example.hydrationtime.databinding.ItemOnboardingBinding
import com.example.hydrationtime.ui.auth.LoginActivity
import com.example.hydrationtime.utils.Constants

/**
 * OnboardingActivity - Affiche les 3 pages d'introduction
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val onboardingPages = listOf(
        OnboardingPage(
            R.drawable.onboarding_1,
            getString(R.string.onboarding_title_1),
            getString(R.string.onboarding_description_1)
        ),
        OnboardingPage(
            R.drawable.onboarding_2,
            getString(R.string.onboarding_title_2),
            getString(R.string.onboarding_description_2)
        ),
        OnboardingPage(
            R.drawable.onboarding_3,
            getString(R.string.onboarding_title_3),
            getString(R.string.onboarding_description_3)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(onboardingPages)
        binding.viewPager.adapter = onboardingAdapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtons(position)
            }
        })
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < onboardingPages.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updateButtons(position: Int) {
        if (position == onboardingPages.size - 1) {
            binding.btnNext.text = getString(R.string.start)
            binding.btnSkip.visibility = View.GONE
        } else {
            binding.btnNext.text = getString(R.string.next)
            binding.btnSkip.visibility = View.VISIBLE
        }
    }

    private fun finishOnboarding() {
        // Marquer que l'onboarding est terminé
        getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(Constants.KEY_FIRST_TIME, false)
            .apply()

        // Aller à l'écran de login
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
