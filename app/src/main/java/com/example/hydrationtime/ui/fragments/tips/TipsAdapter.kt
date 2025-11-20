package com.example.hydrationtime.ui.fragments.tips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.databinding.ItemTipBinding

class TipsAdapter(
    private val tips: List<HydrationTip>,
    private val onShareClick: (HydrationTip) -> Unit
) : RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    inner class TipViewHolder(private val binding: ItemTipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tip: HydrationTip) {
            binding.ivTipImage.setImageResource(tip.imageRes)
            binding.tvTipTitle.text = tip.title
            binding.tvTipDescription.text = tip.description

            binding.btnShare.setOnClickListener {
                onShareClick(tip)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val binding = ItemTipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.bind(tips[position])
    }

    override fun getItemCount(): Int = tips.size
}