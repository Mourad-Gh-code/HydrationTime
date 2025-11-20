package com.example.hydrationtime.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.databinding.ItemNotificationMessageBinding

data class NotificationMessage(
    val id: Int,
    val message: String,
    var isActive: Boolean
)

class NotificationMessageAdapter(
    private val onMessageToggled: (NotificationMessage) -> Unit
) : ListAdapter<NotificationMessage, NotificationMessageAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onMessageToggled)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemNotificationMessageBinding,
        private val onMessageToggled: (NotificationMessage) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: NotificationMessage) {
            binding.tvMessage.text = message.message
            binding.checkboxActive.isChecked = message.isActive

            binding.checkboxActive.setOnCheckedChangeListener { _, isChecked ->
                message.isActive = isChecked
                onMessageToggled(message)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationMessage>() {
        override fun areItemsTheSame(oldItem: NotificationMessage, newItem: NotificationMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationMessage, newItem: NotificationMessage): Boolean {
            return oldItem == newItem
        }
    }
}
