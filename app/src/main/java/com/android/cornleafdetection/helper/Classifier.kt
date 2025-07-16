package com.android.cornleafdetection.helper

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.android.cornleafdetection.data.DiseaseDataResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.InputStream
import java.lang.reflect.Type
import java.nio.MappedByteBuffer

class Classifier(private val context: Context) {
    private var interpreter: Interpreter
    private val inputImageSize = 224
    private val confidenceThreshold = 0.6f

    private val modelPath = "model.tflite"
    private val labelPath = "labels.txt"
    private val jsonPath = "corn_disease_names.json"

    private var labels: List<String>
    private var diseaseDataList: List<DiseaseDataResponse>

    init {
        val model: MappedByteBuffer = FileUtil.loadMappedFile(context, modelPath)
        val options = Interpreter.Options().apply { numThreads = 4 }
        interpreter = Interpreter(model, options)

        Log.d("Classifier", "Model berhasil dimuat.")

        // Load label dari file dan JSON penyakit
        labels = loadLabels()
        diseaseDataList = loadDiseaseDataFromJson()
    }

    fun classifyImage(bitmap: Bitmap): Pair<String, Float?> {
        val tensorImage = preprocessImage(bitmap)
        val outputArray = Array(1) { FloatArray(labels.size) }

        return try {
            interpreter.run(tensorImage.buffer, outputArray)
            val probabilities = outputArray[0]
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
            val confidence = probabilities.getOrNull(maxIndex) ?: 0f
            val predictedLabel = labels.getOrNull(maxIndex) ?: "Tidak dapat Mendeteksi"

            // Kasus 1: model memprediksi "Bukan Daun"
            if (predictedLabel.equals("Bukan Daun", ignoreCase = true)) {
                Log.d("Classifier", "Model memprediksi 'Bukan Daun'. Output dianggap tidak valid.")
                return Pair("Tidak dapat Mendeteksi", null)
            }

            // Kasus 2: confidence rendah
            if (confidence < confidenceThreshold) {
                Log.d("Classifier", "Confidence rendah (< $confidenceThreshold). Output dianggap tidak diketahui.")
                return Pair("Tidak dapat Mendeteksi", null)
            }

            // Kasus 3: prediksi valid
            Log.d("Classifier", "Prediksi: $predictedLabel, Confidence: $confidence")
            return Pair(predictedLabel, confidence)

        } catch (e: Exception) {
            Pair("Tidak Dapat Mendeteksi", 0.0f)
        }
    }

    private fun preprocessImage(bitmap: Bitmap): TensorImage {
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputImageSize, inputImageSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
        return imageProcessor.process(tensorImage)
    }

    // get info penyakit berdasarkan nama
    fun getDiseaseInfoByName(diseaseName: String): DiseaseDataResponse? {
        return diseaseDataList.find {
            it.disease_name.equals(diseaseName, ignoreCase = true)
        }
    }

    // Memuat label dari file teks
    private fun loadLabels(): List<String> {
        return FileUtil.loadLabels(context, labelPath)
    }

    // Memuat data penyakit dari file JSON
    private fun loadDiseaseDataFromJson(): List<DiseaseDataResponse> {
        val inputStream: InputStream = context.assets.open(jsonPath)
        val json = inputStream.bufferedReader().use { it.readText() }
        val type: Type = object : TypeToken<List<DiseaseDataResponse>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun close() {
        interpreter.close()
    }
}