package com.example.hydrationtime.ui.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.databinding.ItemDrinkBreakdownBinding

data class DrinkBreakdownItem(
    val drinkName: String,
    val amount: Float,
    val percentage: Float,
    val color: Int
)

class DrinkBreakdownAdapter : ListAdapter<DrinkBreakdownItem, DrinkBreakdownAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDrinkBreakdownBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemDrinkBreakdownBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DrinkBreakdownItem) {
            binding.tvDrinkName.text = item.drinkName
            binding.tvDrinkAmount.text = String.format("%.2f L", item.amount)
            binding.tvPercentage.text = String.format("%.0f%%", item.percentage)

            // Set color indicator
            val drawable = binding.viewColorIndicator.background as? GradientDrawable
            drawable?.setColor(item.color)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DrinkBreakdownItem>() {
        override fun areItemsTheSame(oldItem: DrinkBreakdownItem, newItem: DrinkBreakdownItem): Boolean {
            return oldItem.drinkName == newItem.drinkName
        }

        override fun areContentsTheSame(oldItem: DrinkBreakdownItem, newItem: DrinkBreakdownItem): Boolean {
            return oldItem == newItem
        }
    }
}
