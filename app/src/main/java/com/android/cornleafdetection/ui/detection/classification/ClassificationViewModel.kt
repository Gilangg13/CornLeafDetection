package com.android.cornleafdetection.ui.detection.classification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.cornleafdetection.data.Detection
import com.android.cornleafdetection.repository.Repository
import kotlinx.coroutines.launch

class ClassificationViewModel(private val repository: Repository) : ViewModel() {
    fun insertDetection(detection: Detection) {
        viewModelScope.launch {
            repository.insertDetection(detection)
            Log.d("ClassificationViewModel", "Detection inserted: $detection")
        }
    }
}