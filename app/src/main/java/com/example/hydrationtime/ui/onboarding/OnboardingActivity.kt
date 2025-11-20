package com.example.hydrationtime.ui.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.ActivityOnboardingBinding
import com.example.hydrationtime.ui.auth.LoginActivity
import com.example.hydrationtime.utils.Constants


/**
 * OnboardingActivity - Affiche les 3 pages d'introduction
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    // Permission launcher for notification permission (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Save notification permission status
        val sharedPrefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(Constants.KEY_NOTIFICATION_ENABLED, isGranted)
            .apply()

        // Continue to login regardless of permission result
        navigateToLogin()
    }

    private val onboardingPages = listOf(
        OnboardingPage(
            R.drawable.ic_water_drop_large,
            // Note: Make sure these strings exist in your strings.xml
            "Suivez votre hydratation",
            "Enregistrez facilement votre consommation d'eau quotidienne"
        ),
        OnboardingPage(
            // [FIX] Changed missing 'onboarding_2' to existing 'ic_goals'
            R.drawable.ic_goals,
            "Définissez vos objectifs",
            "Créez des objectifs personnalisés pour rester hydraté"
        ),
        OnboardingPage(
            // [FIX] Changed missing 'onboarding_3' to existing 'ic_notifications'
            R.drawable.ic_notifications,
            "Recevez des rappels",
            "Des notifications pour vous rappeler de boire de l'eau"
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

        // [FIX] Use attachTo() instead of setViewPager2() for DotsIndicator v5.0
        binding.dotsIndicator.attachTo(binding.viewPager)

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

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    navigateToLogin()
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For Android 12 and below, notifications are enabled by default
            getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.KEY_NOTIFICATION_ENABLED, true)
                .apply()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        // Aller à l'écran de login
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}