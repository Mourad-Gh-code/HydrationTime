package com.example.hydrationtime.ui.fragments.dashboard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hydrationtime.databinding.FragmentDashboardBinding
import com.example.hydrationtime.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class DashboardFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Sensor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // Menu State
    private var isMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Sensors
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // 2. Date Widget
        val today = DateUtils.getTodayDate() // YYYY-MM-DD
        binding.tvDayNumber.text = today.takeLast(2) // Simple extraction
        binding.tvDayName.text = java.text.SimpleDateFormat("EEEE", Locale.getDefault()).format(java.util.Date())

        // 3. FAB Logic
        binding.fabMain.setOnClickListener { toggleFabMenu() }
        binding.fabGlass.setOnClickListener { addWater(200f); toggleFabMenu() }
        binding.fabCup.setOnClickListener { addWater(250f); toggleFabMenu() }
        binding.fabBottle.setOnClickListener { addWater(500f); toggleFabMenu() }

        // 4. Data Observation
        auth.currentUser?.uid?.let { userId ->
            viewModel.loadDashboardData(userId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.todayTotal.observe(viewLifecycleOwner) { total ->
            val current = total ?: 0f
            val goal = viewModel.userDailyGoal.value ?: 2.0f // 2000ml roughly

            // Update Text
            val currentMl = (current * 1000).toInt()
            val goalMl = (goal * 1000).toInt()
            val percent = (current / goal * 100).toInt().coerceIn(0, 100)
            val remainingMl = (goalMl - currentMl).coerceAtLeast(0)

            binding.tvProgressInfo.text = "$currentMl ml • $percent%"
            binding.tvRemaining.text = "Remaining: $remainingMl ml"

            // Update Bubble Progress Bar
            binding.bubbleProgressBar.setProgress(percent.toFloat())

            // Update Water Body View (0.0 to 1.0)
            binding.waterBodyView.setWaterLevel(current / goal)
        }
    }

    private fun addWater(amountMl: Float) {
        auth.currentUser?.uid?.let { uid ->
            viewModel.addWaterIntake(uid, amountMl / 1000f) // Convert ml to Liters
            Toast.makeText(context, "Added ${amountMl.toInt()}ml", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFabMenu() {
        isMenuOpen = !isMenuOpen

        // Rotate FAB icon (+ to X)
        val rotation = if (isMenuOpen) 45f else 0f
        binding.fabMain.animate()
            .rotation(rotation)
            .setInterpolator(OvershootInterpolator())
            .setDuration(300)
            .start()

        // Hide history button when menu is open
        binding.btnHistory.animate()
            .alpha(if (isMenuOpen) 0f else 1f)
            .setDuration(200)
            .start()

        if (isMenuOpen) {
            binding.layoutFabMenu.visibility = View.VISIBLE

            // Animate each button with stagger
            binding.fabGlass.apply {
                translationY = 50f
                alpha = 0f
                animate()
                    .translationY(0f)
                    .alpha(1f)
                    .rotation(360f)
                    .setDuration(300)
                    .start()
            }

            binding.fabCup.apply {
                translationY = 50f
                alpha = 0f
                animate()
                    .translationY(0f)
                    .alpha(1f)
                    .rotation(360f)
                    .setStartDelay(50)
                    .setDuration(300)
                    .start()
            }

            binding.fabBottle.apply {
                translationY = 50f
                alpha = 0f
                animate()
                    .translationY(0f)
                    .alpha(1f)
                    .rotation(360f)
                    .setStartDelay(100)
                    .setDuration(300)
                    .start()
            }
        } else {
            // Fade out and hide
            binding.fabGlass.animate().alpha(0f).setDuration(150).start()
            binding.fabCup.animate().alpha(0f).setDuration(150).start()
            binding.fabBottle.animate().alpha(0f).setDuration(150).withEndAction {
                binding.layoutFabMenu.visibility = View.INVISIBLE
            }.start()
        }
    }

    // --- Sensor Logic ---
    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Map Accelerator X (-10 to 10) to rotation angle (-45 to 45)
            // This creates the horizon stabilization effect
            val angle = it.values[0] * -5
            binding.waterBodyView.setTilt(angle)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}