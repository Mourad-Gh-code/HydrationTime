package com.example.hydrationtime.ui.fragments.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hydrationtime.databinding.FragmentStatisticsBinding
import com.example.hydrationtime.ui.adapters.DrinkBreakdownAdapter
import com.example.hydrationtime.ui.adapters.DrinkBreakdownItem
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import androidx.lifecycle.ViewModelProvider
import com.example.hydrationtime.data.local.database.AppDatabase
import com.example.hydrationtime.data.local.dao.HourlyConsumption
import com.example.hydrationtime.data.local.dao.DailyConsumptionByType
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StatisticsViewModel
    private lateinit var drinkBreakdownAdapter: DrinkBreakdownAdapter

    private var currentPeriod = Period.DAILY

    enum class Period {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    private val drinkColors = mapOf(
        "Water" to Color.parseColor("#29B6F6"),
        "Tea" to Color.parseColor("#66BB6A"),
        "Coffee" to Color.parseColor("#8D6E63"),
        "Juice" to Color.parseColor("#FF9800"),
        "Smoothie" to Color.parseColor("#E91E63"),
        "Milk" to Color.parseColor("#EEEEEE")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel with DAO
        val database = AppDatabase.getDatabase(requireContext())
        val consumptionLogDao = database.consumptionLogDao()
        val factory = StatisticsViewModelFactory(consumptionLogDao)
        viewModel = ViewModelProvider(this, factory)[StatisticsViewModel::class.java]

        setupTabs()
        setupChart()
        setupRecyclerView()
        observeViewModel()

        // Load initial data
        loadData(Period.DAILY)
    }

    private fun setupRecyclerView() {
        drinkBreakdownAdapter = DrinkBreakdownAdapter()
        binding.rvDrinkBreakdown.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = drinkBreakdownAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("D"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("W"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("M"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Y"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadData(Period.DAILY)
                    1 -> loadData(Period.WEEKLY)
                    2 -> loadData(Period.MONTHLY)
                    3 -> loadData(Period.YEARLY)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setPinchZoom(false)
            setScaleEnabled(false)

            // X-Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.GRAY
            }

            // Left Y-Axis
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#20000000")
                textColor = Color.GRAY
                axisMinimum = 0f
            }

            // Right Y-Axis (disable)
            axisRight.isEnabled = false

            // Legend
            legend.isEnabled = true
            legend.textColor = Color.GRAY

            // Animation
            animateY(800, Easing.EaseInOutQuad)
        }
    }

    private fun loadData(period: Period) {
        currentPeriod = period

        // Animate transition
        binding.chartContainer.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                // Update chart based on period
                updateChartForPeriod(period)

                binding.chartContainer.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
            .start()

        // Load data from ViewModel
        viewModel.loadDataForPeriod(period)
    }

    private fun updateChartForPeriod(period: Period) {
        when (period) {
            Period.DAILY -> {
                binding.tvPeriodTitle.text = "Today"
                updateDailyChart()
            }
            Period.WEEKLY -> {
                binding.tvPeriodTitle.text = "This Week"
                updateWeeklyChart()
            }
            Period.MONTHLY -> {
                binding.tvPeriodTitle.text = "This Month"
                updateMonthlyChart()
            }
            Period.YEARLY -> {
                binding.tvPeriodTitle.text = "This Year"
                updateYearlyChart()
            }
        }
    }

    private fun updateDailyChart() {
        // This will be replaced by real data observation
        // Placeholder: show empty chart until data loads
        binding.barChart.clear()
        binding.barChart.invalidate()
    }

    private fun updateDailyChartWithRealData(hourlyData: List<HourlyConsumption>) {
        if (hourlyData.isEmpty()) {
            // No data - show empty chart
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for today")
            binding.barChart.invalidate()
            return
        }

        // Group data by hour and drink type
        val hourlyMap = mutableMapOf<Int, MutableMap<String, Float>>()

        hourlyData.forEach { consumption ->
            val hour = consumption.hour.toIntOrNull() ?: 0
            if (!hourlyMap.containsKey(hour)) {
                hourlyMap[hour] = mutableMapOf()
            }
            hourlyMap[hour]!![consumption.drinkName] = consumption.amount / 1000f // Convert to liters
        }

        // Create entries for each drink type
        val drinkTypeEntries = mutableMapOf<String, MutableList<BarEntry>>()

        // Get all hours from 0 to 23
        val allHours = (0..23).toList()
        val hourLabels = allHours.map { hour ->
            when {
                hour == 0 -> "12AM"
                hour < 12 -> "${hour}AM"
                hour == 12 -> "12PM"
                else -> "${hour - 12}PM"
            }
        }

        // Populate entries
        allHours.forEachIndexed { index, hour ->
            val hourData = hourlyMap[hour] ?: emptyMap()
            hourData.forEach { (drinkName, amount) ->
                if (!drinkTypeEntries.containsKey(drinkName)) {
                    drinkTypeEntries[drinkName] = mutableListOf()
                }
                drinkTypeEntries[drinkName]!!.add(BarEntry(index.toFloat(), amount))
            }
        }

        // Create datasets
        val dataSets = mutableListOf<BarDataSet>()
        drinkTypeEntries.forEach { (drinkName, entries) ->
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, drinkName).apply {
                    color = drinkColors[drinkName] ?: Color.GRAY
                    setDrawValues(false)
                }
                dataSets.add(dataSet)
            }
        }

        if (dataSets.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for today")
            binding.barChart.invalidate()
            return
        }

        val barData = BarData(dataSets as List<BarDataSet>)
        barData.barWidth = 0.25f

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(hourLabels)
            xAxis.labelCount = 12 // Show every other hour
            if (dataSets.size > 1) {
                groupBars(0f, 0.5f, 0.05f)
            }
            invalidate()
        }
    }

    private fun updateWeeklyChart() {
        // Placeholder - will be replaced by real data
        binding.barChart.clear()
        binding.barChart.invalidate()
    }

    private fun updateWeeklyChartWithRealData(dailyData: List<DailyConsumptionByType>) {
        if (dailyData.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for this week")
            binding.barChart.invalidate()
            return
        }

        // Get last 7 days
        val calendar = Calendar.getInstance()
        val daysOfWeek = (0..6).map { offset ->
            calendar.add(Calendar.DAY_OF_YEAR, if (offset == 0) 0 else -1)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(calendar.time)
        }.reversed()

        val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Group data by date and drink type
        val dateMap = mutableMapOf<String, MutableMap<String, Float>>()
        dailyData.forEach { consumption ->
            if (!dateMap.containsKey(consumption.date)) {
                dateMap[consumption.date] = mutableMapOf()
            }
            dateMap[consumption.date]!![consumption.drinkName] = consumption.amount / 1000f
        }

        // Create entries for each drink type
        val drinkTypeEntries = mutableMapOf<String, MutableList<BarEntry>>()

        daysOfWeek.forEachIndexed { index, date ->
            val dayData = dateMap[date] ?: emptyMap()
            dayData.forEach { (drinkName, amount) ->
                if (!drinkTypeEntries.containsKey(drinkName)) {
                    drinkTypeEntries[drinkName] = mutableListOf()
                }
                drinkTypeEntries[drinkName]!!.add(BarEntry(index.toFloat(), amount))
            }
        }

        // Create datasets
        val dataSets = mutableListOf<BarDataSet>()
        drinkTypeEntries.forEach { (drinkName, entries) ->
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, drinkName).apply {
                    color = drinkColors[drinkName] ?: Color.GRAY
                    setDrawValues(false)
                }
                dataSets.add(dataSet)
            }
        }

        if (dataSets.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for this week")
            binding.barChart.invalidate()
            return
        }

        val barData = BarData(dataSets as List<BarDataSet>)
        barData.barWidth = 0.25f

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
            xAxis.labelCount = dayLabels.size
            if (dataSets.size > 1) {
                groupBars(0f, 0.3f, 0.02f)
            }
            invalidate()
        }
    }

    private fun updateMonthlyChart() {
        // Placeholder - will be replaced by real data
        binding.barChart.clear()
        binding.barChart.invalidate()
    }

    private fun updateMonthlyChartWithRealData(dailyData: List<DailyConsumptionByType>) {
        if (dailyData.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for this month")
            binding.barChart.invalidate()
            return
        }

        // Group by date and sum all drink types
        val dateMap = mutableMapOf<String, Float>()
        dailyData.forEach { consumption ->
            dateMap[consumption.date] = (dateMap[consumption.date] ?: 0f) + (consumption.amount / 1000f)
        }

        // Create entries
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        dateMap.keys.sorted().forEachIndexed { index, date ->
            val amount = dateMap[date] ?: 0f
            entries.add(BarEntry(index.toFloat(), amount))
            // Extract day from date (yyyy-MM-dd -> dd)
            labels.add(date.substringAfterLast("-"))
        }

        if (entries.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for this month")
            binding.barChart.invalidate()
            return
        }

        val dataSet = BarDataSet(entries, "Total Consumption").apply {
            color = drinkColors["Water"] ?: Color.BLUE
            setDrawValues(false)
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelCount = minOf(10, labels.size)
            invalidate()
        }
    }

    private fun updateYearlyChart() {
        // Placeholder - will be replaced by real data
        binding.barChart.clear()
        binding.barChart.invalidate()
    }

    private fun updateYearlyChartWithRealData(dailyData: List<DailyConsumptionByType>) {
        if (dailyData.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for this year")
            binding.barChart.invalidate()
            return
        }

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        // Group data by month and drink type
        val monthMap = mutableMapOf<Int, MutableMap<String, Float>>()
        dailyData.forEach { consumption ->
            // Extract month from date (yyyy-MM-dd)
            val month = consumption.date.substring(5, 7).toIntOrNull()?.minus(1) ?: 0
            if (!monthMap.containsKey(month)) {
                monthMap[month] = mutableMapOf()
            }
            val currentAmount = monthMap[month]!![consumption.drinkName] ?: 0f
            monthMap[month]!![consumption.drinkName] = currentAmount + (consumption.amount / 1000f)
        }

        // Create entries for each drink type
        val drinkTypeEntries = mutableMapOf<String, MutableList<BarEntry>>()

        (0..11).forEach { month ->
            val monthData = monthMap[month] ?: emptyMap()
            monthData.forEach { (drinkName, amount) ->
                if (!drinkTypeEntries.containsKey(drinkName)) {
                    drinkTypeEntries[drinkName] = mutableListOf()
                }
                drinkTypeEntries[drinkName]!!.add(BarEntry(month.toFloat(), amount))
            }
        }

        // Create datasets
        val dataSets = mutableListOf<BarDataSet>()
        drinkTypeEntries.forEach { (drinkName, entries) ->
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, drinkName).apply {
                    color = drinkColors[drinkName] ?: Color.GRAY
                    setDrawValues(false)
                }
                dataSets.add(dataSet)
            }
        }

        if (dataSets.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No consumption data for this year")
            binding.barChart.invalidate()
            return
        }

        val barData = BarData(dataSets as List<BarDataSet>)
        barData.barWidth = 0.25f

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(months)
            xAxis.labelCount = months.size
            if (dataSets.size > 1) {
                groupBars(0f, 0.3f, 0.02f)
            }
            invalidate()
        }
    }

    private fun observeViewModel() {
        viewModel.totalConsumption.observe(viewLifecycleOwner) { total ->
            binding.tvTotalValue.text = String.format("%.2f L", total ?: 0f)
        }

        viewModel.avgDaily.observe(viewLifecycleOwner) { avg ->
            binding.tvAvgDailyValue.text = String.format("%.2f L", avg ?: 0f)
        }

        viewModel.mostLoggedDrink.observe(viewLifecycleOwner) { drink ->
            binding.tvMostLoggedValue.text = drink ?: "None"
        }

        viewModel.goalAchievement.observe(viewLifecycleOwner) { achievement ->
            binding.tvGoalAchievementValue.text = "${achievement ?: 0} days"
        }

        viewModel.drinkDistribution.observe(viewLifecycleOwner) { distribution ->
            updateDrinkTypeChart(distribution)
            updateDrinkBreakdownList(distribution)
        }

        viewModel.hourlyData.observe(viewLifecycleOwner) { hourlyData ->
            updateDailyChartWithRealData(hourlyData)
        }

        viewModel.dailyDataInRange.observe(viewLifecycleOwner) { dailyData ->
            when (currentPeriod) {
                Period.WEEKLY -> updateWeeklyChartWithRealData(dailyData)
                Period.MONTHLY -> updateMonthlyChartWithRealData(dailyData)
                Period.YEARLY -> updateYearlyChartWithRealData(dailyData)
                else -> {}
            }
        }
    }

    private fun updateDrinkTypeChart(distribution: Map<String, Float>) {
        // Update circular chart with drink distribution
        binding.drinkTypeChart.setData(distribution)
    }

    private fun updateDrinkBreakdownList(distribution: Map<String, Float>) {
        val total = distribution.values.sum()
        if (total == 0f) {
            drinkBreakdownAdapter.submitList(emptyList())
            return
        }

        val items = distribution.map { (drinkName, amount) ->
            DrinkBreakdownItem(
                drinkName = drinkName,
                amount = amount,
                percentage = (amount / total) * 100,
                color = drinkColors[drinkName] ?: Color.GRAY
            )
        }.sortedByDescending { it.amount }

        drinkBreakdownAdapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
