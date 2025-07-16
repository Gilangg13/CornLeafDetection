package com.android.cornleafdetection.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "detection")
@Parcelize
data class Detection(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "imageUri")
    var uri: String,

    @ColumnInfo(name = "disease_name")
    var disease_name: String? = null,

    @ColumnInfo(name = "scientific_name")
    var scientific_name: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "confidence")
    var confidence: Float = 0.0F,

    @ColumnInfo(name = "symptoms")
    var symptoms: String? = null,

    @ColumnInfo(name = "causes")
    var causes: String? = null,

    @ColumnInfo(name = "treatment")
    var treatment: String? = null,

    @ColumnInfo(name = "detected_at")
    var detectedAt: String

) : Parcelable
