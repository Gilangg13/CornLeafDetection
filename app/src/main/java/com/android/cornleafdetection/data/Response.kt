package com.android.cornleafdetection.data

data class DiseaseDataResponse(
    val disease_name: String,
    val scientific_name: String,
    val description: String,
    val symptoms: String,
    val causes: String,
    val treatment: String
)