package com.example.hydrationtime.ui.fragments.profile

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.hydrationtime.data.local.database.AppDatabase
import com.example.hydrationtime.data.local.entities.User
import com.example.hydrationtime.databinding.FragmentProfileBinding
import com.example.hydrationtime.ui.auth.LoginActivity
import com.example.hydrationtime.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

/**
 * ProfileFragment - Profil utilisateur
 * Design inspiré de l'image 4 (partie basse)
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()

        // Charger les données utilisateur
        auth.currentUser?.uid?.let { userId ->
            viewModel.loadUserProfile(userId)
        }
    }

    private fun setupUI() {
        // Notifications
        binding.layoutNotifications.setOnClickListener {
            toggleNotifications()
        }

        // Objectif quotidien
        binding.layoutDailyGoal.setOnClickListener {
            // Naviguer vers l'onglet Objectifs
            // Ou ouvrir un dialog pour modifier l'objectif
        }

        // Historique
        binding.layoutHistory.setOnClickListener {
            Toast.makeText(requireContext(), "Historique - À implémenter", Toast.LENGTH_SHORT).show()
        }

        // Déconnexion
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                binding.tvUserEmail.text = it.email
                binding.tvDailyGoalValue.text = String.format("%.1fL", it.dailyGoal)
            }
        }

        viewModel.notificationsEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.switchNotifications.isChecked = enabled
        }
    }

    private fun toggleNotifications() {
        val newState = !binding.switchNotifications.isChecked
        binding.switchNotifications.isChecked = newState
        viewModel.setNotificationsEnabled(newState)

        val message = if (newState) {
            "Notifications activées"
        } else {
            "Notifications désactivées"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Déconnexion")
            .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
            .setPositiveButton("Oui") { _, _ ->
                logout()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun logout() {
        auth.signOut()

        // Aller à LoginActivity
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
