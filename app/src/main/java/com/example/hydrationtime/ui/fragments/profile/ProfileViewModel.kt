package com.example.hydrationtime.ui.fragments.profile
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.hydrationtime.data.local.database.AppDatabase
import com.example.hydrationtime.data.local.entities.User
import com.example.hydrationtime.utils.Constants

/**
 * ProfileViewModel - ViewModel pour le profil
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val _userProfile = MediatorLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    private val _notificationsEnabled = MediatorLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled

    private val sharedPrefs = application.getSharedPreferences(Constants.PREFS_NAME, android.content.Context.MODE_PRIVATE)

    init {
        _notificationsEnabled.value = sharedPrefs.getBoolean(Constants.KEY_NOTIFICATION_ENABLED, true)
    }

    fun loadUserProfile(userId: String) {
        val userSource = database.userDao().getUserById(userId)
        _userProfile.addSource(userSource) { user ->
            _userProfile.value = user
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        sharedPrefs.edit()
            .putBoolean(Constants.KEY_NOTIFICATION_ENABLED, enabled)
            .apply()

        // TODO: Activer/désactiver WorkManager pour les rappels
    }
}