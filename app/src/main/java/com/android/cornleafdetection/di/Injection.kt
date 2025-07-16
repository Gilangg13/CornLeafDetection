package com.android.cornleafdetection.di

import android.content.Context
import com.android.cornleafdetection.data.DetectionRoomDatabase
import com.android.cornleafdetection.repository.Repository

object Injection {
    fun provideRepository(context: Context): Repository {
        val database = DetectionRoomDatabase.getDatabase(context)
        return Repository(database)
    }
}