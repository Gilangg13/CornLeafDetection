package com.android.cornleafdetection.ui.information

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.cornleafdetection.R
import com.android.cornleafdetection.data.CornLeafDisease
import com.android.cornleafdetection.databinding.ActivityDetailDiseaseBinding

@Suppress("DEPRECATION")
class DetailDiseaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDiseaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_detail_disease)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Detail Informasi Penyakit"
        }
        binding.toolbar.setNavigationOnClickListener { finish() }

        val disease = intent.getSerializableExtra(EXTRA_DISEASE) as? CornLeafDisease

        disease?.let {
            binding.diseaseName.text = it.name
            binding.diseaseScientific.text = it.scientificName
            binding.diseaseDescription.text = it.description
            binding.diseaseSymptoms.text = it.symptoms
            binding.diseaseCause.text = it.cause
            binding.diseaseTreatment.text = it.treatment
            binding.diseaseImage.setImageResource(it.imageResId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_DISEASE = "extra_disease"
    }
}