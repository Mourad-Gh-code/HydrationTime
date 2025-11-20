package com.example.hydrationtime.ui.fragments.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.FragmentSettingsBinding
import com.example.hydrationtime.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Permission launcher for notification permission (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        binding.switchNotifications.isChecked = isGranted
        saveNotificationPreference(isGranted)

        if (!isGranted) {
            // Show dialog to explain why notifications are important
            showNotificationPermissionDialog()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadCurrentSettings()
    }

    private fun setupUI() {
        // Theme Selection
        binding.cardTheme.setOnClickListener {
            showThemeDialog()
        }

        // Language Selection
        binding.cardLanguage.setOnClickListener {
            showLanguageDialog()
        }

        // Daily Goal
        binding.cardDailyGoal.setOnClickListener {
            showDailyGoalDialog()
        }

        // Notifications
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            handleNotificationToggle(isChecked)
        }

        // Notification Messages
        binding.cardNotificationMessages.setOnClickListener {
            openNotificationMessagesFragment()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadCurrentSettings() {
        // Load current theme
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.tvThemeValue.text = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> getString(R.string.dark_mode)
            else -> getString(R.string.light_mode)
        }

        // Load current language
        val currentLocale = Locale.getDefault().language
        binding.tvLanguageValue.text = when (currentLocale) {
            "en" -> getString(R.string.english)
            "fr" -> getString(R.string.french)
            "ar" -> getString(R.string.arabic)
            else -> getString(R.string.english)
        }

        // Load daily goal
        val sharedPrefs = requireContext().getSharedPreferences("hydration_prefs", 0)
        val dailyGoal = sharedPrefs.getInt("daily_goal_ml", 2000)
        binding.tvDailyGoalValue.text = "$dailyGoal ml"

        // Load notification preference - check actual permission status for Android 13+
        val notificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            sharedPrefs.getBoolean("notifications_enabled", true)
        }

        // Temporarily remove listener to avoid triggering it when setting initial value
        binding.switchNotifications.setOnCheckedChangeListener(null)
        binding.switchNotifications.isChecked = notificationsEnabled
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            handleNotificationToggle(isChecked)
        }
    }

    private fun showThemeDialog() {
        val themes = arrayOf(
            getString(R.string.light_mode),
            getString(R.string.dark_mode)
        )

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.theme))
            .setItems(themes) { _, which ->
                when (which) {
                    0 -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            .show()
    }

    private fun setTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        binding.tvThemeValue.text = when (mode) {
            AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.dark_mode)
            else -> getString(R.string.light_mode)
        }

        // Save preference
        val sharedPrefs = requireContext().getSharedPreferences("hydration_prefs", 0)
        sharedPrefs.edit().putInt("theme_mode", mode).apply()

        // Recreate activity to apply theme
        requireActivity().recreate()
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.english),
            getString(R.string.french),
            getString(R.string.arabic)
        )

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.language))
            .setItems(languages) { _, which ->
                when (which) {
                    0 -> setLanguage("en")
                    1 -> setLanguage("fr")
                    2 -> setLanguage("ar")
                }
            }
            .show()
    }

    private fun setLanguage(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)

        binding.tvLanguageValue.text = when (languageCode) {
            "en" -> getString(R.string.english)
            "fr" -> getString(R.string.french)
            "ar" -> getString(R.string.arabic)
            else -> getString(R.string.english)
        }

        // Save preference
        val sharedPrefs = requireContext().getSharedPreferences("hydration_prefs", 0)
        sharedPrefs.edit().putString("language", languageCode).apply()

        Toast.makeText(requireContext(), "Language changed", Toast.LENGTH_SHORT).show()
    }

    private fun showDailyGoalDialog() {
        val goals = arrayOf("1500 ml", "2000 ml", "2500 ml", "3000 ml", "3500 ml")
        val goalValues = arrayOf(1500, 2000, 2500, 3000, 3500)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.daily_goal_setting))
            .setItems(goals) { _, which ->
                setDailyGoal(goalValues[which])
            }
            .show()
    }

    private fun setDailyGoal(goalMl: Int) {
        binding.tvDailyGoalValue.text = "$goalMl ml"

        // Save preference
        val sharedPrefs = requireContext().getSharedPreferences("hydration_prefs", 0)
        sharedPrefs.edit().putInt("daily_goal_ml", goalMl).apply()

        Toast.makeText(requireContext(), "Daily goal updated", Toast.LENGTH_SHORT).show()
    }

    private fun handleNotificationToggle(isChecked: Boolean) {
        if (isChecked) {
            // User wants to enable notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check if permission is already granted
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Permission already granted
                        saveNotificationPreference(true)
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                        // Show explanation dialog
                        showNotificationRationaleDialog()
                    }
                    else -> {
                        // Request permission
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                // For Android 12 and below, just save preference
                saveNotificationPreference(true)
            }
        } else {
            // User wants to disable notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // On Android 13+, guide user to system settings to disable
                showDisableNotificationDialog()
            } else {
                saveNotificationPreference(false)
            }
        }
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        val sharedPrefs = requireContext().getSharedPreferences("hydration_prefs", 0)
        sharedPrefs.edit().putBoolean("notifications_enabled", enabled).apply()

        Toast.makeText(
            requireContext(),
            if (enabled) "Notifications enabled" else "Notifications disabled",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNotificationRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Notifications")
            .setMessage("Notifications help you stay hydrated by reminding you to drink water throughout the day. Would you like to enable notifications?")
            .setPositiveButton("Enable") { _, _ ->
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Not Now") { _, _ ->
                binding.switchNotifications.isChecked = false
            }
            .show()
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Notification Permission Denied")
            .setMessage("You can enable notifications later in your device settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                binding.switchNotifications.isChecked = false
            }
            .show()
    }

    private fun showDisableNotificationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Disable Notifications")
            .setMessage("To disable notifications, please go to your device settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                binding.switchNotifications.isChecked = true
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout))
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openNotificationMessagesFragment() {
        val fragment = com.example.hydrationtime.ui.fragments.notifications.NotificationMessagesFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
