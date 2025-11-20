package com.example.hydrationtime.ui.fragments.tips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hydrationtime.R
import com.example.hydrationtime.databinding.FragmentTipsBinding
import com.example.hydrationtime.databinding.ItemTipBinding

/**
 * TipsFragment - Conseils d'hydratation
 * Design inspiré de l'image 4 (partie haute)
 */
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
        // Liste des conseils
        val tips = getTipsList()

        tipsAdapter = TipsAdapter(tips) { tip ->
            shareTip(tip)
        }

        binding.recyclerViewTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun getTipsList(): List<HydrationTip> {
        return listOf(
            HydrationTip(
                R.drawable.tip_1,
                "Buvez dès le matin",
                "Commencez votre journée avec un grand verre d'eau pour réhydrater votre corps après le sommeil."
            ),
            HydrationTip(
                R.drawable.tip_2,
                "Mangez des fruits",
                "Les fruits comme la pastèque, le melon et les oranges contiennent beaucoup d'eau et aident à l'hydratation."
            ),
            HydrationTip(
                R.drawable.tip_3,
                "Hydratez-vous pendant l'exercice",
                "Buvez de l'eau avant, pendant et après vos séances d'entraînement pour compenser la perte de liquide."
            ),
            HydrationTip(
                R.drawable.tip_4,
                "Buvez avant les repas",
                "Prendre un verre d'eau 30 minutes avant chaque repas aide à la digestion et à l'hydratation."
            ),
            HydrationTip(
                R.drawable.tip_5,
                "Utilisez une bouteille réutilisable",
                "Gardez toujours une bouteille d'eau à portée de main pour vous rappeler de boire régulièrement."
            ),
            HydrationTip(
                R.drawable.tip_6,
                "Écoutez votre corps",
                "La soif est un signal important. Ne l'ignorez jamais et buvez dès que vous ressentez le besoin."
            )
        )
    }

    private fun shareTip(tip: HydrationTip) {
        val shareText = "${tip.title}\n\n${tip.description}\n\n#Hydratation #Santé"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Partager le conseil"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
