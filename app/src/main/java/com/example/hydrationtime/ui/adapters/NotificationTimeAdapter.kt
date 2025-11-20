package com.example.hydrationtime.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.databinding.ItemNotificationTimeBinding

data class NotificationTime(
    val id: Int,
    val hour: Int,
    val minute: Int,
    var isActive: Boolean
) {
    fun getFormattedTime(): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        return String.format("%d:%02d %s", displayHour, minute, amPm)
    }
}

class NotificationTimeAdapter(
    private val onTimeToggled: (NotificationTime) -> Unit,
    private val onTimeDeleted: (NotificationTime) -> Unit
) : ListAdapter<NotificationTime, NotificationTimeAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onTimeToggled, onTimeDeleted)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemNotificationTimeBinding,
        private val onTimeToggled: (NotificationTime) -> Unit,
        private val onTimeDeleted: (NotificationTime) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(time: NotificationTime) {
            binding.tvTime.text = time.getFormattedTime()
            binding.switchActive.isChecked = time.isActive

            binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
                time.isActive = isChecked
                onTimeToggled(time)
            }

            binding.btnDelete.setOnClickListener {
                onTimeDeleted(time)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationTime>() {
        override fun areItemsTheSame(oldItem: NotificationTime, newItem: NotificationTime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationTime, newItem: NotificationTime): Boolean {
            return oldItem == newItem
        }
    }
}
