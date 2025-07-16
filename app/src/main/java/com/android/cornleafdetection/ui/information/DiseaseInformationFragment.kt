package com.android.cornleafdetection.ui.information

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.cornleafdetection.adapter.DiseaseAdapter
import com.android.cornleafdetection.data.DiseaseData
import com.android.cornleafdetection.databinding.FragmentMaterialDiseaseBinding

class DiseaseInformationFragment : Fragment() {

    private var _binding: FragmentMaterialDiseaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialDiseaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            title = "Informasi Penyakit"
        }

        val diseaseList = DiseaseData.getDiseaseList()
        val adapter = DiseaseAdapter(diseaseList) { disease ->
            val intent = Intent(requireContext(), DetailDiseaseActivity::class.java).apply {
                putExtra(DetailDiseaseActivity.EXTRA_DISEASE, disease)
            }
            startActivity(intent)
        }

        binding.recyclerViewDisease.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDisease.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}