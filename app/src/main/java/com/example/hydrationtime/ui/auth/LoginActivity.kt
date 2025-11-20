package com.example.hydrationtime.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.hydrationtime.databinding.ActivityLoginBinding
import com.example.hydrationtime.ui.main.MainActivity

/**
 * LoginActivity - Écran de connexion
 * Design basé sur l'image fournie avec fond dégradé bleu
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Bouton "Continue" pour se connecter
        binding.btnContinue.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.signIn(email, password)
        }

        // Lien "Sign Up" pour aller vers l'inscription
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Le lien "Login" est déjà sur cette page (désactivé)
        binding.tvLogin.isEnabled = false
        binding.tvLogin.alpha = 0.5f
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    showLoading(true)
                }
                is AuthState.Success -> {
                    showLoading(false)
                    // Connexion réussie -> MainActivity
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
        binding.btnContinue.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }
}