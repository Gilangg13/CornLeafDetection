package com.android.cornleafdetection.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.cornleafdetection.data.Detection
import com.android.cornleafdetection.repository.Repository

class HistoryViewModel(repository: Repository) : ViewModel() {
    val getAllDetection: LiveData<List<Detection>> = repository.getAllDetection()
}