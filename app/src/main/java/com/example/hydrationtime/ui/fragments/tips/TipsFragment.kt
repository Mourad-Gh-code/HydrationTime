package com.example.hydrationtime.ui.fragments.tips

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.FragmentTipsBinding

class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tipsAdapter: TipsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val tips = getTipsList()

        // Updated Adapter instantiation with the new click listener
        tipsAdapter = TipsAdapter(
            tips = tips,
            onShareClick = { tip -> shareTip(tip) },
            onTipClick = { tip -> openTipUrl(tip) } // [ADDED] Open URL action
        )

        binding.recyclerViewTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun getTipsList(): List<HydrationTip> {
        return listOf(
            HydrationTip(
                R.drawable.ic_water_drop, // Using default icon if specific drawable missing
                "Buvez dès le matin",
                "Commencez votre journée avec un grand verre d'eau.",
                "https://www.healthline.com/nutrition/drinking-water-in-the-morning"
            ),
            HydrationTip(
                R.drawable.ic_water_drop,
                "Mangez des fruits",
                "Les fruits comme la pastèque aident à l'hydratation.",
                "https://www.medicalnewstoday.com/articles/325958"
            ),
            HydrationTip(
                R.drawable.ic_water_drop,
                "Importance de l'hydratation",
                "Pourquoi est-il si important de boire de l'eau ?",
                "https://www.youtube.com/watch?v=9iMGFqMmUFs" // Video link
            )
            // Add more tips as needed
        )
    }

    private fun openTipUrl(tip: HydrationTip) {
        tip.url?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } ?: run {
            Toast.makeText(requireContext(), "Pas de lien disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareTip(tip: HydrationTip) {
        val shareText = "${tip.title}\n\n${tip.description}\n\nApprenez-en plus sur HydrationTime!"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Partager"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}