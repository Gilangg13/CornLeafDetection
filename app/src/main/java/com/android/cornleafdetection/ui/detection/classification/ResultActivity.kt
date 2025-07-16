package com.android.cornleafdetection.ui.detection.classification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.android.cornleafdetection.MainActivity
import com.android.cornleafdetection.R
import com.android.cornleafdetection.data.Detection
import com.android.cornleafdetection.data.DiseaseDataResponse
import com.android.cornleafdetection.databinding.ActivityResultBinding
import com.android.cornleafdetection.ui.ViewModelFactory
import com.android.cornleafdetection.ui.getCurrentTimestamp
import com.google.gson.Gson
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var classificationViewModel: ClassificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Hasil Deteksi"
        }
        binding.toolbar.setNavigationOnClickListener { finish() }

        classificationViewModel = obtainViewModel(this@ResultActivity)

        // Menampilkan hasil gambar dan analisis
        val resultImage = intent.getStringExtra(RESULT_IMAGE)
        val imageUri = resultImage?.let { Uri.parse(it) }

        val resultTitle = intent.getStringExtra(RESULT_TITLE)
        val resultConfidence = intent.getFloatExtra(RESULT_CONFIDENCE, 0.0f)

        // Parse disease data
        val diseaseDataJson = intent.getStringExtra(RESULT_DISEASE_DATA)
        val diseaseData = diseaseDataJson?.takeIf { it.isNotEmpty() }?.let {
            try {
                Gson().fromJson(it, DiseaseDataResponse::class.java)
            } catch (e: Exception) {
                null
            }
        }

        imageUri?.let { binding.resultImage.setImageURI(it) }
        binding.resultDisease.text = resultTitle
        binding.resultConfidence.text = getString(
            R.string.analyze_score,
            NumberFormat.getPercentInstance().format(resultConfidence)
        )

        when (resultTitle) {
            "Daun Sehat" -> {
                // Hanya tampilkan label, confidence, dan deskripsi sehat
                binding.layoutDisease.visibility = View.VISIBLE
                binding.layoutScientificName.visibility = View.GONE
                binding.layoutConfidence.visibility = View.VISIBLE

                binding.resultDescriptionLabel.visibility = View.VISIBLE
                binding.resultDescription.visibility = View.VISIBLE
                binding.resultDescription.text =
                    "Daun dalam kondisi sehat, tidak ditemukan gejala penyakit."

                binding.resultSymptomsLabel.visibility = View.GONE
                binding.resultSymptoms.visibility = View.GONE
                binding.resultCausesLabel.visibility = View.GONE
                binding.resultCauses.visibility = View.GONE
                binding.resultTreatmentLabel.visibility = View.GONE
                binding.resultTreatment.visibility = View.GONE
            }

            "Tidak Dapat Mendeteksi" -> {
                // Hanya tampilkan label dan confidence saja
                binding.layoutDisease.visibility = View.VISIBLE
                binding.layoutScientificName.visibility = View.GONE
                binding.layoutConfidence.visibility = View.GONE

                binding.resultDescriptionLabel.visibility = View.GONE
                binding.resultDescription.visibility = View.GONE
                binding.resultSymptomsLabel.visibility = View.GONE
                binding.resultSymptoms.visibility = View.GONE
                binding.resultCausesLabel.visibility = View.GONE
                binding.resultCauses.visibility = View.GONE
                binding.resultTreatmentLabel.visibility = View.GONE
                binding.resultTreatment.visibility = View.GONE
            }

            else -> {
                // Penyakit terdeteksi, tampilkan semua info
                binding.layoutDisease.visibility = View.VISIBLE
                binding.layoutScientificName.visibility = View.VISIBLE
                binding.layoutConfidence.visibility = View.VISIBLE

                binding.resultDescriptionLabel.visibility = View.VISIBLE
                binding.resultDescription.visibility = View.VISIBLE
                binding.resultSymptomsLabel.visibility = View.VISIBLE
                binding.resultSymptoms.visibility = View.VISIBLE
                binding.resultCausesLabel.visibility = View.VISIBLE
                binding.resultCauses.visibility = View.VISIBLE
                binding.resultTreatmentLabel.visibility = View.VISIBLE
                binding.resultTreatment.visibility = View.VISIBLE

                binding.resultScientificName.text = diseaseData?.scientific_name
                binding.resultDescription.text = diseaseData?.description
                binding.resultSymptoms.text = diseaseData?.symptoms
                binding.resultCauses.text = diseaseData?.causes
                binding.resultTreatment.text = diseaseData?.treatment
            }
        }

        // Save result to history
        val analyzeResult = Detection(
            uri = resultImage ?: "",
            disease_name = resultTitle ?: "",
            scientific_name = diseaseData?.scientific_name ?: "",
            description = diseaseData?.description ?: "",
            confidence = resultConfidence,
            symptoms = diseaseData?.symptoms ?: "",
            causes = diseaseData?.causes ?: "",
            treatment = diseaseData?.treatment ?: "",
            detectedAt = getCurrentTimestamp(),
        )

        classificationViewModel.insertDetection(analyzeResult)

        binding.btnFinish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ClassificationViewModel {
        val factory = ViewModelFactory(activity.applicationContext)
        return ViewModelProvider(activity, factory)[ClassificationViewModel::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val RESULT_IMAGE = "RESULT_IMAGE"
        const val RESULT_TITLE = "RESULT_TITLE"
        const val RESULT_CONFIDENCE = "RESULT_CONFIDENCE"
        const val RESULT_DISEASE_DATA = "RESULT_DISEASE_DATA"
    }
}