package com.example.hydrationtime.ui.auth

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.hydrationtime.databinding.ActivitySignUpBinding // [FIX] Correct Import
import com.example.hydrationtime.ui.main.MainActivity
import com.example.hydrationtime.utils.DateUtils
import java.util.*

/**
 * SignUpActivity - Écran d'inscription
 */

@SuppressLint("CustomSplashScreen")
class SignUpActivity : AppCompatActivity() {

    // [FIX] Changed ActivitySignupBinding to ActivitySignUpBinding
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: AuthViewModel by viewModels()
    private var selectedBirthdayTimestamp: Long = 0

    // Debug auto-fill feature
    private var logoClickCount = 0
    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [FIX] Changed to ActivitySignUpBinding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Debug auto-fill: Triple click on logo to auto-fill form
        binding.logoImageView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 500) { // 500ms gap allowed
                logoClickCount++
            } else {
                logoClickCount = 1
            }
            lastClickTime = currentTime

            if (logoClickCount == 3) {
                // Auto-fill with test data
                binding.etName.setText("Test User")
                binding.etEmail.setText("testuser@hydration.com")
                binding.etPassword.setText("password123")
                binding.etConfirmPassword.setText("password123")

                // Set birthday to 25 years ago
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.YEAR, -25)
                selectedBirthdayTimestamp = calendar.timeInMillis
                binding.etBirthday.setText(DateUtils.getDisplayDate(DateUtils.getDateString(selectedBirthdayTimestamp)))

                logoClickCount = 0
                Toast.makeText(this, "🎉 Debug Mode: Form Auto-Filled!", Toast.LENGTH_SHORT).show()

                // Auto-submit after 1 second
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.btnSignUp.performClick()
                }, 1000)
            }
        }

        // Sélection de la date de naissance
        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }

        // Bouton d'inscription
        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Validation
            if (password != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedBirthdayTimestamp == 0L) {
                Toast.makeText(this, "Veuillez sélectionner votre date de naissance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signUp(email, password, name, selectedBirthdayTimestamp)
        }

        // Lien vers Login
        binding.tvLogin.setOnClickListener {
            finish() // Retour à LoginActivity
            // Apply reverse slide animation: current screen slides out to right, previous screen slides in from left
            overridePendingTransition(com.example.hydrationtime.R.anim.slide_in_left, com.example.hydrationtime.R.anim.slide_out_right)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18) // Par défaut il y a 18 ans

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedBirthdayTimestamp = selectedCalendar.timeInMillis

                // Afficher la date sélectionnée
                binding.etBirthday.setText(DateUtils.getDisplayDate(DateUtils.getDateString(selectedBirthdayTimestamp)))

                // Vérifier l'âge
                if (!DateUtils.isValidAge(selectedBirthdayTimestamp)) {
                    Toast.makeText(
                        this,
                        "Vous devez avoir au moins 18 ans",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Limiter la sélection de date
        datePicker.datePicker.maxDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    showLoading(true)
                }
                is AuthState.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show()
                    // Aller directement à MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is AuthState.Idle -> {
                    showLoading(false)
                }
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignUp.isEnabled = !isLoading
        binding.etName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
        binding.etBirthday.isEnabled = !isLoading
    }

}