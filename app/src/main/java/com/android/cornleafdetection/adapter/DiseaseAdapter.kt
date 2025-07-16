package com.android.cornleafdetection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.cornleafdetection.adapter.DiseaseAdapter.DiseaseViewHolder
import com.android.cornleafdetection.data.CornLeafDisease
import com.android.cornleafdetection.databinding.ItemMaterialDiseaseBinding

class DiseaseAdapter(
    private val diseaseList: List<CornLeafDisease>,
    private val onItemClick: (CornLeafDisease) -> Unit
) : RecyclerView.Adapter<DiseaseViewHolder>() {

    inner class DiseaseViewHolder(private val binding: ItemMaterialDiseaseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(disease: CornLeafDisease) {
            binding.diseaseImage.setImageResource(disease.imageResId)
            binding.diseaseName.text = disease.name
            binding.diseaseDescription.text = disease.description

            binding.root.setOnClickListener {
                onItemClick(disease)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val binding = ItemMaterialDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiseaseViewHolder(binding)
    }

    override fun getItemCount(): Int = diseaseList.size

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        holder.bind(diseaseList[position])
    }
}