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
                R.drawable.morining_drink,
                "Buvez dès le matin",
                "Commencez votre journée avec un grand verre d'eau pour réveiller votre métabolisme et réhydrater votre corps.",
                "https://www.healthline.com/nutrition/drinking-water-in-the-morning"
            ),
            HydrationTip(
                R.drawable.fruits,
                "Mangez des fruits hydratants",
                "Les fruits comme la pastèque, le concombre et les agrumes contiennent plus de 90% d'eau et contribuent à votre hydratation.",
                "https://www.medicalnewstoday.com/articles/325958"
            ),
            HydrationTip(
                R.drawable.dink_before,
                "Buvez avant les repas",
                "Boire un verre d'eau 30 minutes avant les repas peut améliorer la digestion et vous aider à mieux réguler votre appétit.",
                "https://www.healthline.com/nutrition/drinking-water-before-meals"
            ),
            HydrationTip(
                R.drawable.eight,
                "Suivez la règle 8x8",
                "Essayez de boire 8 verres de 250ml d'eau par jour. C'est une bonne base pour rester hydraté, mais ajustez selon vos besoins.",
                "https://www.mayoclinic.org/healthy-lifestyle/nutrition-and-healthy-eating/in-depth/water/art-20044256"
            ),
            HydrationTip(
                R.drawable.spots,
                "Hydratez-vous pendant l'exercice",
                "Buvez de l'eau avant, pendant et après l'exercice. Pour une activité intense, prévoyez 200-300ml toutes les 15-20 minutes.",
                "https://www.healthline.com/nutrition/how-much-water-should-you-drink-per-day"
            ),
            HydrationTip(
                R.drawable.chaud,
                "Attention par temps chaud",
                "Augmentez votre consommation d'eau lorsqu'il fait chaud ou que vous transpirez davantage pour compenser les pertes.",
                "https://www.cdc.gov/healthywater/drinking/nutrition/index.html"
            ),
            HydrationTip(
                R.drawable.neccessary,
                "Importance de l'hydratation",
                "L'eau régule votre température, transporte les nutriments et aide à éliminer les toxines. Découvrez tous ses bienfaits !",
                "https://www.youtube.com/watch?v=9iMGFqMmUFs"
            )
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