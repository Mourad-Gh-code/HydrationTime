package com.example.hydrationtime.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hydrationtime.data.local.database.AppDatabase
import com.example.hydrationtime.data.repository.AuthRepository
import com.example.hydrationtime.utils.DateUtils
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

/**
 * AuthViewModel - ViewModel pour la gestion de l'authentification
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val authRepository = AuthRepository(database.userDao())

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Inscription d'un nouvel utilisateur
     */
    fun signUp(email: String, password: String, name: String, birthdayTimestamp: Long) {
        // Validation des champs
        if (name.isBlank()) {
            _errorMessage.value = "Le nom est requis"
            return
        }

        if (email.isBlank()) {
            _errorMessage.value = "L'email est requis"
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Le mot de passe doit contenir au moins 6 caractères"
            return
        }

        // Validation de l'âge (>= 18 ans)
        if (!DateUtils.isValidAge(birthdayTimestamp)) {
            _errorMessage.value = "Vous devez avoir au moins 18 ans pour utiliser cette application"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.signUp(email, password, name, birthdayTimestamp)

            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Erreur d'inscription")
                _errorMessage.value = exception.message ?: "Erreur d'inscription"
            }
        }
    }

    /**
     * Connexion d'un utilisateur existant
     */
    fun signIn(email: String, password: String) {
        // Validation des champs
        if (email.isBlank()) {
            _errorMessage.value = "L'email est requis"
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "Le mot de passe est requis"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.signIn(email, password)

            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Erreur de connexion")
                _errorMessage.value = exception.message ?: "Email ou mot de passe incorrect"
            }
        }
    }

    /**
     * Réinitialiser l'état d'authentification
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

/**
 * États possibles de l'authentification
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}