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
            val percent = (current / goal * 100).toInt()
            binding.tvProgressInfo.text = "${(current * 1000).toInt()} ml • $percent%"
            binding.progressBar.progress = percent

            // Update Physics Body (0.0 to 1.0)
            binding.bodyWaterView.setWaterLevel(current / goal)
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

        val rotation = if (isMenuOpen) 45f else 0f
        binding.fabMain.animate().rotation(rotation).setInterpolator(OvershootInterpolator()).start()

        binding.layoutFabMenu.visibility = if (isMenuOpen) View.VISIBLE else View.INVISIBLE

        if (isMenuOpen) {
            // Spin out animation
            binding.fabCup.translationY = 50f
            binding.fabCup.animate().translationY(0f).rotation(360f).setDuration(300).start()

            binding.fabBottle.translationY = 50f
            binding.fabBottle.animate().translationY(0f).rotation(360f).setStartDelay(50).setDuration(300).start()
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
            val angle = it.values[0] * -5
            binding.bodyWaterView.setTilt(angle)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}