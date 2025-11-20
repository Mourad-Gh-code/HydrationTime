package com.example.hydrationtime.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.hydrationtime.databinding.ActivitySplashBinding
import com.example.hydrationtime.ui.onboarding.OnboardingActivity
import com.example.hydrationtime.ui.auth.LoginActivity
import com.example.hydrationtime.ui.main.MainActivity
import com.example.hydrationtime.utils.Constants
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animation de chargement (fade in then fade out)
        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        // Fade in animation for logo
        binding.logoImageView.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .withEndAction {
                    // Start fade out after fade in completes
                    startFadeOutAnimation()
                }
                .start()
        }

        // Animation du texte
        binding.appNameTextView.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(200)
                .start()
        }
    }

    private fun startFadeOutAnimation() {
        // Fade out animation for logo
        binding.logoImageView.animate()
            .alpha(0f)
            .scaleX(0.5f)
            .scaleY(0.5f)
            .setDuration(600)
            .setStartDelay(800)
            .start()

        // Fade out animation for text
        binding.appNameTextView.animate()
            .alpha(0f)
            .translationY(-30f)
            .setDuration(600)
            .setStartDelay(800)
            .withEndAction {
                navigateToNextScreen()
            }
            .start()
    }

    private fun navigateToNextScreen() {
        val sharedPrefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        val isFirstTime = sharedPrefs.getBoolean(Constants.KEY_FIRST_TIME, true)

        val intent = when {
            isFirstTime -> {
                // Première ouverture -> Onboarding
                Intent(this, OnboardingActivity::class.java)
            }
            auth.currentUser != null -> {
                // Utilisateur connecté -> MainActivity
                Intent(this, MainActivity::class.java)
            }
            else -> {
                // Pas connecté -> LoginActivity
                Intent(this, LoginActivity::class.java)
            }
        }

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}