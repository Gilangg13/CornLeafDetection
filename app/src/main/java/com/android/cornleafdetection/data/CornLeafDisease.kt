package com.android.cornleafdetection.data

import java.io.Serializable

data class CornLeafDisease(
    val name: String,
    val scientificName: String,
    val description: String,
    val symptoms: String,
    val cause: String,
    val treatment: String,
    val imageResId: Int
) : Serializable
