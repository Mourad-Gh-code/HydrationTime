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

        // Animation de chargement
        startLoadingAnimation()

        // Délai de 2 secondes puis navigation
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun startLoadingAnimation() {
        // Animation du logo
        binding.logoImageView.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(1000)
                .start()
        }

        // Animation du texte
        binding.appNameTextView.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1000)
                .setStartDelay(300)
                .start()
        }
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
        finish()
    }
}