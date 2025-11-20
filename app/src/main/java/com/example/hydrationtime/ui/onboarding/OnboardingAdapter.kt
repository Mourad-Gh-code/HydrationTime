package com.example.hydrationtime.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.databinding.ItemOnboardingBinding

/**
 * OnboardingPage - Modèle de données pour une page d'onboarding
 */
data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

/**
 * OnboardingAdapter - Adapter pour le ViewPager2
 */
class OnboardingAdapter(
    private val pages: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage) {
            binding.imageView.setImageResource(page.imageRes)
            binding.titleTextView.text = page.title
            binding.descriptionTextView.text = page.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size
}