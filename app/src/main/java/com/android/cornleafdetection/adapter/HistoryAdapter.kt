package com.android.cornleafdetection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.cornleafdetection.R
import com.android.cornleafdetection.data.Detection
import com.bumptech.glide.Glide
import java.text.NumberFormat

class HistoryAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<Detection, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    interface ItemClickListener {
        fun onItemClick(analyze: Detection)
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val analyzeHistoryImg: ImageView = item.findViewById(R.id.analyze_history_image)
        private val analyzeHistoryType: TextView = item.findViewById(R.id.analyze_history_type)
        private val analyzeHistoryScore: TextView = item.findViewById(R.id.analyze_history_score)

        fun bind(analyze: Detection) {
            Glide.with(itemView.context)
                .load(analyze.uri)
                .placeholder(R.drawable.ic_place_holder)
                .centerCrop()
                .into(analyzeHistoryImg)

            analyzeHistoryType.text = analyze.disease_name
            analyzeHistoryScore.text = itemView.context.getString(
                R.string.analyze_score,
                NumberFormat.getPercentInstance().format(analyze.confidence)
            )

            itemView.setOnClickListener {
                itemClickListener.onItemClick(analyze)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Detection>() {
            override fun areItemsTheSame(oldItem: Detection, newItem: Detection): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: Detection, newItem: Detection): Boolean {
                return oldItem == newItem
            }
        }
    }
}