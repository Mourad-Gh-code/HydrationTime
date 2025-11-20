package com.example.hydrationtime.ui.fragments.dashboard


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hydrationtime.R
import com.example.hydrationtime.data.local.entities.DailyIntake
import com.example.hydrationtime.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

/**
 * DashboardFragment - Tableau de bord avec graphique
 * Design inspiré de l'image 2
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()

        // Charger les données
        auth.currentUser?.uid?.let { userId ->
            viewModel.loadDashboardData(userId)
        }
    }

    private fun setupUI() {
        // Bouton flottant pour ajouter de l'eau
        binding.fabAddWater.setOnClickListener {
            showAddWaterDialog()
        }

        setupChart()
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setMaxVisibleValueCount(7)
            setPinchZoom(false)
            setScaleEnabled(false)

            // Axe X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.WHITE
            }

            // Axe Y gauche
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                textColor = Color.WHITE
            }

            // Axe Y droit (désactivé)
            axisRight.isEnabled = false

            // Légende
            legend.isEnabled = false
        }
    }

    private fun observeViewModel() {
        // Observer la consommation totale du jour
        viewModel.todayTotal.observe(viewLifecycleOwner) { total ->
            val dailyGoal = viewModel.userDailyGoal.value ?: 2.0f
            binding.tvWaterConsumed.text = String.format("%.1fL / %.1fL", total ?: 0f, dailyGoal)

            // Mettre à jour la silhouette (niveau d'hydratation)
            updateHydrationLevel(total ?: 0f, dailyGoal)
        }

        // Observer les données de la semaine
        viewModel.weeklyData.observe(viewLifecycleOwner) { weeklyData ->
            updateChart(weeklyData)
        }

        // Observer l'objectif quotidien
        viewModel.userDailyGoal.observe(viewLifecycleOwner) { goal ->
            val total = viewModel.todayTotal.value ?: 0f
            binding.tvWaterConsumed.text = String.format("%.1fL / %.1fL", total, goal)
        }
    }

    private fun updateHydrationLevel(consumed: Float, goal: Float) {
        val percentage = (consumed / goal * 100).toInt()
        // Ici, vous pouvez animer la silhouette selon le pourcentage
        // Pour l'instant, on affiche juste un indicateur visuel
        binding.tvHydrationPercent.text = "$percentage%"
    }

    private fun updateChart(weeklyData: List<DailyIntake>) {
        if (weeklyData.isEmpty()) return

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        weeklyData.forEachIndexed { index, dailyIntake ->
            entries.add(BarEntry(index.toFloat(), dailyIntake.totalAmount))
            // Formater la date (jour seulement)
            labels.add(dailyIntake.date.substring(8, 10))
        }

        val dataSet = BarDataSet(entries, "Consommation").apply {
            color = Color.parseColor("#4A90E2")
            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelCount = labels.size
            animateY(1000)
            invalidate()
        }
    }

    private fun showAddWaterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_water, null)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etWaterAmount)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ajouter de l'eau")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { _, _ ->
                val amount = etAmount.text.toString().toFloatOrNull()
                if (amount != null && amount > 0) {
                    auth.currentUser?.uid?.let { userId ->
                        viewModel.addWaterIntake(userId, amount)
                        Toast.makeText(requireContext(), "Ajouté : ${amount}L", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Quantité invalide", Toast.LENGTH_SHORT).show()
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
 * DashboardViewModel - ViewModel pour le tableau de bord
 */
