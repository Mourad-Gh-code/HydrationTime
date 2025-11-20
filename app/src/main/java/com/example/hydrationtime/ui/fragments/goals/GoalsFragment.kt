package com.example.hydrationtime.ui.fragments.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.R
import com.example.hydrationtime.data.local.entities.Goal
import com.example.hydrationtime.databinding.FragmentGoalsBinding
import com.example.hydrationtime.databinding.ItemGoalBinding
import com.example.hydrationtime.utils.DateUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

/**
 * GoalsFragment - Gestion des objectifs
 * Design inspiré de l'image 3
 */
class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var goalsAdapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()

        // Charger les données
        auth.currentUser?.uid?.let { userId ->
            viewModel.loadGoals(userId)
        }
    }

    private fun setupUI() {
        // RecyclerView pour l'historique des objectifs
        goalsAdapter = GoalsAdapter()
        binding.recyclerViewGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalsAdapter
        }

        // Bouton pour définir un nouvel objectif
        binding.btnSetGoal.setOnClickListener {
            showSetGoalDialog()
        }
    }

    private fun observeViewModel() {
        // Observer la progression actuelle
        viewModel.currentProgress.observe(viewLifecycleOwner) { progress ->
            val percentage = (progress * 100).toInt()
            binding.progressCircular.progress = percentage
            binding.tvProgressPercent.text = "$percentage%\nl'objectif atteint"

            // Mettre à jour le texte de progression
            val consumed = viewModel.todayConsumed.value ?: 0f
            val goal = viewModel.dailyGoal.value ?: 2.0f
            binding.tvProgressDetail.text = String.format("%.1fL / %.1fL", consumed, goal)
        }

        // Observer l'historique des objectifs
        viewModel.goalsHistory.observe(viewLifecycleOwner) { goals ->
            goalsAdapter.submitList(goals)
        }

        // Observer l'objectif quotidien
        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            binding.etDailyGoal.setText(goal.toString())
        }
    }

    private fun showSetGoalDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_goal, null)
        val etGoal = dialogView.findViewById<TextInputEditText>(R.id.etGoalAmount)

        // Pré-remplir avec l'objectif actuel
        val currentGoal = viewModel.dailyGoal.value ?: 2.0f
        etGoal.setText(currentGoal.toString())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Définir un objectif quotidien")
            .setView(dialogView)
            .setPositiveButton("Enregistrer") { _, _ ->
                val newGoal = etGoal.text.toString().toFloatOrNull()
                if (newGoal != null && newGoal > 0) {
                    auth.currentUser?.uid?.let { userId ->
                        viewModel.setDailyGoal(userId, newGoal)
                        Toast.makeText(requireContext(), "Objectif mis à jour !", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Objectif invalide", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * GoalsAdapter - Adapter pour l'historique des objectifs
 */
class GoalsAdapter : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    private var goals = listOf<Goal>()

    fun submitList(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    inner class GoalViewHolder(private val binding: ItemGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goal) {
            binding.tvGoalDate.text = DateUtils.getDisplayDate(goal.date)
            binding.tvGoalAmount.text = String.format("%.1fL / %.1fL", goal.achievedAmount, goal.targetAmount)

            // Statut
            if (goal.achieved) {
                binding.tvGoalStatus.text = "atteint ✓"
                binding.tvGoalStatus.setTextColor(binding.root.context.getColor(R.color.green))
            } else {
                binding.tvGoalStatus.text = "non atteint ✗"
                binding.tvGoalStatus.setTextColor(binding.root.context.getColor(R.color.red))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int = goals.size
}
