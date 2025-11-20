package com.example.hydrationtime.ui.fragments.notifications

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hydrationtime.databinding.FragmentNotificationMessagesBinding
import com.example.hydrationtime.notifications.NotificationScheduler
import com.example.hydrationtime.ui.adapters.NotificationMessage
import com.example.hydrationtime.ui.adapters.NotificationMessageAdapter
import com.example.hydrationtime.ui.adapters.NotificationTime
import com.example.hydrationtime.ui.adapters.NotificationTimeAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class NotificationMessagesFragment : Fragment() {

    private var _binding: FragmentNotificationMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: NotificationMessageAdapter
    private lateinit var timeAdapter: NotificationTimeAdapter

    private val messages = mutableListOf<NotificationMessage>()
    private val times = mutableListOf<NotificationTime>()

    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences("hydration_prefs", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupMessagesRecyclerView()
        setupTimesRecyclerView()
        loadSavedData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupMessagesRecyclerView() {
        messageAdapter = NotificationMessageAdapter { message ->
            saveMessages()
        }
        binding.rvActiveMessages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun setupTimesRecyclerView() {
        timeAdapter = NotificationTimeAdapter(
            onTimeToggled = { time ->
                saveTimes()
                scheduleNotifications()
            },
            onTimeDeleted = { time ->
                times.remove(time)
                timeAdapter.submitList(times.toList())
                saveTimes()
                scheduleNotifications()
                Toast.makeText(requireContext(), "Time removed", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvNotificationTimes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timeAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnAddCustomMessage.setOnClickListener {
            showAddMessageDialog()
        }

        binding.btnAddTime.setOnClickListener {
            showAddTimeDialog()
        }
    }

    private fun loadSavedData() {
        // Load messages
        val gson = Gson()
        val messagesJson = sharedPrefs.getString("notification_messages", null)
        if (messagesJson != null) {
            val type = object : TypeToken<List<NotificationMessage>>() {}.type
            messages.clear()
            messages.addAll(gson.fromJson(messagesJson, type))
        } else {
            // Default messages
            messages.addAll(
                listOf(
                    NotificationMessage(1, "Ana, time to drink more water!", true),
                    NotificationMessage(2, "Time for another glass of water!", true),
                    NotificationMessage(3, "Ana, it's water time!", true),
                    NotificationMessage(4, "Have some water to stay hydrated!", true),
                    NotificationMessage(5, "Ana, time for a break with a cup of water!", true)
                )
            )
        }
        messageAdapter.submitList(messages.toList())

        // Load times
        val timesJson = sharedPrefs.getString("notification_times", null)
        if (timesJson != null) {
            val type = object : TypeToken<List<NotificationTime>>() {}.type
            times.clear()
            times.addAll(gson.fromJson(timesJson, type))
        }
        timeAdapter.submitList(times.toList())
    }

    private fun saveMessages() {
        val gson = Gson()
        val json = gson.toJson(messages)
        sharedPrefs.edit().putString("notification_messages", json).apply()
    }

    private fun saveTimes() {
        val gson = Gson()
        val json = gson.toJson(times)
        sharedPrefs.edit().putString("notification_times", json).apply()
    }

    private fun showAddMessageDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter your custom message"
            setPadding(50, 40, 50, 40)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Custom Message")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val message = input.text.toString()
                if (message.isNotBlank()) {
                    val newId = (messages.maxByOrNull { it.id }?.id ?: 0) + 1
                    messages.add(NotificationMessage(newId, message, true))
                    messageAdapter.submitList(messages.toList())
                    saveMessages()
                    Toast.makeText(requireContext(), "Message added", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddTimeDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val newId = (times.maxByOrNull { it.id }?.id ?: 0) + 1
            times.add(NotificationTime(newId, selectedHour, selectedMinute, true))
            timeAdapter.submitList(times.toList())
            saveTimes()
            scheduleNotifications()
            Toast.makeText(requireContext(), "Time added", Toast.LENGTH_SHORT).show()
        }, hour, minute, false).show()
    }

    private fun scheduleNotifications() {
        val activeTimes = times.filter { it.isActive }
        NotificationScheduler.scheduleNotifications(requireContext(), activeTimes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
