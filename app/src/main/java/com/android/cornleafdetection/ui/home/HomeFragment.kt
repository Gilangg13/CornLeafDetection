package com.android.cornleafdetection.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.cornleafdetection.R
import com.android.cornleafdetection.adapter.DiseaseAdapter
import com.android.cornleafdetection.data.DiseaseData
import com.android.cornleafdetection.databinding.FragmentHomeBinding
import com.android.cornleafdetection.ui.information.DetailDiseaseActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.homeToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnStartScan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanFragment)
        }

        binding.seeAllButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_materialDiseaseFragment)
        }

        setupDiseaseRecyclerView()
    }

    private fun setupDiseaseRecyclerView() {
        val diseaseList = DiseaseData.getDiseaseList()
        val adapter = DiseaseAdapter(diseaseList) { disease ->
            val intent = Intent(requireContext(), DetailDiseaseActivity::class.java).apply {
                putExtra(DetailDiseaseActivity.EXTRA_DISEASE, disease)
            }
            startActivity(intent)
        }

        binding.rvDisease.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDisease.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).setSupportActionBar(null)
        _binding = null
    }
}