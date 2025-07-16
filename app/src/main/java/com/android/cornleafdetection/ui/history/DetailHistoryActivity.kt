package com.android.cornleafdetection.ui.history

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.cornleafdetection.R
import com.android.cornleafdetection.data.Detection
import com.android.cornleafdetection.databinding.ActivityDetailHistoryBinding
import java.text.NumberFormat

@Suppress("DEPRECATION")
class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Detail Riwayat"
        }
        binding.toolbar.setNavigationOnClickListener { finish() }

        val analyze = intent.getParcelableExtra<Detection>(EXTRA_ANALYZE_ID)

        analyze?.let { data ->
            binding.detailHistoryImage.setImageURI(Uri.parse(data.uri))
            binding.detailHistoryDiseaseName.text = data.disease_name
            binding.detailHistoryConfidence.text = getString(
                R.string.analyze_score,
                NumberFormat.getPercentInstance().format(data.confidence)
            )

            when (data.disease_name) {
                "Daun Sehat" -> {
                    // Tampilkan info minimal
                    binding.layoutDisease.visibility = View.VISIBLE
                    binding.layoutScientificName.visibility = View.GONE
                    binding.layoutConfidence.visibility = View.VISIBLE

                    binding.detailHistoryDescriptionLabel.visibility = View.VISIBLE
                    binding.detailHistoryDescription.visibility = View.VISIBLE
                    binding.detailHistoryDescription.text =
                        "Daun dalam kondisi sehat, tidak ditemukan gejala penyakit."

                    binding.detailHistorySymptomsLabel.visibility = View.GONE
                    binding.detailHistorySymptoms.visibility = View.GONE
                    binding.detailHistoryCausesLabel.visibility = View.GONE
                    binding.detailHistoryCauses.visibility = View.GONE
                    binding.detailHistoryTreatmentLabel.visibility = View.GONE
                    binding.detailHistoryTreatment.visibility = View.GONE

                    binding.detailHistoryDetectionTime.text = data.detectedAt
                }

                "Tidak Dapat Mendeteksi" -> {
                    // Hanya tampilkan label dan confidence
                    binding.layoutDisease.visibility = View.VISIBLE
                    binding.layoutScientificName.visibility = View.GONE
                    binding.layoutConfidence.visibility = View.VISIBLE
                    binding.detailHistoryDescriptionLabel.visibility = View.GONE
                    binding.detailHistoryDescription.visibility = View.GONE
                    binding.detailHistorySymptomsLabel.visibility = View.GONE
                    binding.detailHistorySymptoms.visibility = View.GONE
                    binding.detailHistoryCausesLabel.visibility = View.GONE
                    binding.detailHistoryCauses.visibility = View.GONE
                    binding.detailHistoryTreatmentLabel.visibility = View.GONE
                    binding.detailHistoryTreatment.visibility = View.GONE

                    binding.detailHistoryDetectionTime.text = data.detectedAt
                }

                else -> {
                    // Penyakit terdeteksi
                    binding.layoutDisease.visibility = View.VISIBLE
                    binding.layoutScientificName.visibility = View.VISIBLE
                    binding.layoutConfidence.visibility = View.VISIBLE

                    binding.detailHistoryScientificName.text = data.scientific_name
                    binding.detailHistoryDescriptionLabel.visibility = View.VISIBLE
                    binding.detailHistoryDescription.visibility = View.VISIBLE
                    binding.detailHistoryDescription.text = data.description

                    binding.detailHistorySymptomsLabel.visibility = View.VISIBLE
                    binding.detailHistorySymptoms.visibility = View.VISIBLE
                    binding.detailHistorySymptoms.text = data.symptoms

                    binding.detailHistoryCausesLabel.visibility = View.VISIBLE
                    binding.detailHistoryCauses.visibility = View.VISIBLE
                    binding.detailHistoryCauses.text = data.causes

                    binding.detailHistoryTreatmentLabel.visibility = View.VISIBLE
                    binding.detailHistoryTreatment.visibility = View.VISIBLE
                    binding.detailHistoryTreatment.text = data.treatment

                    binding.detailHistoryDetectionTime.text = data.detectedAt
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ANALYZE_ID = "extra_analyze"
    }
}
