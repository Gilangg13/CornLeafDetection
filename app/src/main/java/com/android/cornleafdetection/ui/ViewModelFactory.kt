package com.android.cornleafdetection.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.cornleafdetection.di.Injection
import com.android.cornleafdetection.ui.history.HistoryViewModel
import com.android.cornleafdetection.ui.detection.classification.ClassificationViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClassificationViewModel::class.java)) {
            return ClassificationViewModel(Injection.provideRepository(context)) as T
        }

        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}