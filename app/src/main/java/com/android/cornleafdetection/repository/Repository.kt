package com.android.cornleafdetection.repository

import androidx.lifecycle.LiveData
import com.android.cornleafdetection.data.Detection
import com.android.cornleafdetection.data.DetectionRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val detectionRoomDatabase: DetectionRoomDatabase) {
    private val detectionDao = detectionRoomDatabase.detectionDao()

    fun getAllDetection(): LiveData<List<Detection>> {
        return detectionDao.getAllDetections()
    }

    suspend fun insertDetection(detection: Detection) {
        withContext(Dispatchers.IO) {
            detectionDao.insert(detection)
        }
    }
}