package com.example.hydrationtime.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.hydrationtime.ui.main.MainActivity
import com.example.hydrationtime.utils.DateUtils
import java.util.*

/**
 * SignUpActivity - Écran d'inscription
 */
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels()
    private var selectedBirthdayTimestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
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