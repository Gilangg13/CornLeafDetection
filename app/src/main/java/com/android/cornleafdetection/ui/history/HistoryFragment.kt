package com.android.cornleafdetection.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.cornleafdetection.adapter.HistoryAdapter
import com.android.cornleafdetection.data.Detection
import com.android.cornleafdetection.databinding.FragmentHistoryBinding
import com.android.cornleafdetection.ui.ViewModelFactory

class HistoryFragment : Fragment(), HistoryAdapter.ItemClickListener {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter
    private var detectionList: List<Detection> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            title = "Riwayat Deteksi"
        }

        historyViewModel = obtainViewModel(this)

        historyAdapter = HistoryAdapter(this)
        binding.historyRv.adapter = historyAdapter
        binding.historyRv.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRv.setHasFixedSize(true)

        // Observasi data dari ViewModel
        historyViewModel.getAllDetection.observe(viewLifecycleOwner) { detections ->
            detectionList = detections
            historyAdapter.submitList(detectionList)

            // Jika list kosong, tampilkan teks "tidak ada data"
            binding.tvEmptyState.visibility =
                if (detectionList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun obtainViewModel(fragment: Fragment): HistoryViewModel {
        val factory = ViewModelFactory(fragment.requireContext())
        return ViewModelProvider(fragment, factory)[HistoryViewModel::class.java]
    }

    override fun onItemClick(analyze: Detection) {
        val intent = Intent(requireContext(), DetailHistoryActivity::class.java)
        intent.putExtra(
            DetailHistoryActivity.EXTRA_ANALYZE_ID,
            analyze
        )
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}