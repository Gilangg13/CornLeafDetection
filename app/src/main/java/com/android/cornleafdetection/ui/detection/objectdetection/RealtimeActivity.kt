package com.android.cornleafdetection.ui.detection.objectdetection

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.cornleafdetection.R
import com.android.cornleafdetection.data.DiseaseDataResponse
import com.android.cornleafdetection.databinding.ActivityRealtimeBinding
import com.android.cornleafdetection.helper.Classifier
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class RealtimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRealtimeBinding
    private lateinit var classifier: Classifier
    private lateinit var cameraExecutor: ExecutorService

    private var lastAnalyzedTime = 0L
    private val ANALYZE_INTERVAL_MS = 200L // jeda antar prediksi 1 detik

    private var isCameraFrozen = false
    private var latestBitmap: Bitmap? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private var isActivityActive = true

    private val predictionBuffer = ArrayDeque<Pair<String, Float>>(5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRealtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Deteksi Realtime"
        }

        classifier = Classifier(this)
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.btnAnalyze.isEnabled = false

        if (allPermissionsGranted()) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnAnalyze.setOnClickListener {
            if (!isCameraFrozen && latestBitmap != null && predictionBuffer.isNotEmpty()) {
                isCameraFrozen = true

                val (label, confidence) = getStablePrediction()
                val confidencePct = confidence.let { "%.2f%%".format(it * 100) } ?: ""

                binding.resultText.text =
                    if (label == "Tidak dapat Mendeteksi") label else "$label ($confidencePct)"

                // Load disease data and match
                val matchedInfo = classifier.getDiseaseInfoByName(label)
                showBottomSheet(matchedInfo)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) openCamera()
        else Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(224, 224))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                        processImageProxy(imageProxy)
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e("RealTime", "Gagal bind kamera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        if (!isActivityActive) {
            imageProxy.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAnalyzedTime < ANALYZE_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastAnalyzedTime = currentTime

        if (!isCameraFrozen) {
            val bitmap = imageProxy.toBitmap()
            if (bitmap != null) {
                latestBitmap = bitmap
                val (label, confidence) = classifier.classifyImage(bitmap)

                synchronized(predictionBuffer) {
                    if (predictionBuffer.size >= 5) predictionBuffer.removeFirst()
                    predictionBuffer.addLast(label to (confidence ?: 0f))
                }

                val (stableLabel, stableConfidence) = getStablePrediction()

                runOnUiThread {
                    if (stableLabel == "Tidak dapat Mendeteksi") {
                        binding.resultText.text = "Tidak dapat Mendeteksi"
                        binding.btnAnalyze.isEnabled = false
                    } else {
                        val confText = "%.2f%%".format(stableConfidence * 100)
                        binding.resultText.text = "$stableLabel ($confText)"
                        binding.btnAnalyze.isEnabled = true
                    }
                }
            }
        }
        imageProxy.close()
    }

    private fun getStablePrediction(): Pair<String, Float> {
        val counts = predictionBuffer.groupingBy { it.first }.eachCount()
        val mostCommon = counts.maxByOrNull { it.value }?.key ?: "Tidak dapat Mendeteksi"
        val averageConfidence = predictionBuffer
            .filter { it.first == mostCommon }
            .map { it.second }
            .average()
            .toFloat()

        return mostCommon to averageConfidence
    }

    private fun ImageProxy.toBitmap(): Bitmap? {
        return try {
            val yBuffer = planes[0].buffer
            val uBuffer = planes[1].buffer
            val vBuffer = planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = android.graphics.YuvImage(
                nv21, android.graphics.ImageFormat.NV21, width, height, null
            )
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 90, out)
            val imageBytes = out.toByteArray()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e("RealTime", "Konversi ke Bitmap gagal: ${e.message}")
            null
        }
    }

    private fun showBottomSheet(info: DiseaseDataResponse?) {
        if (bottomSheetDialog?.isShowing == true || info == null) return

        bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(R.layout.bottom_sheet_deskripsi)

            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            val tvScientificName = findViewById<TextView>(R.id.tvScientificName)
            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            val tvSymptoms = findViewById<TextView>(R.id.tvSymptoms)
            val tvCauses = findViewById<TextView>(R.id.tvCauses)
            val tvTreatment = findViewById<TextView>(R.id.tvTreatment)

            tvTitle?.text = info.disease_name

            // Jika yang terdeteksi adalah daun sehat, tampilkan pesan khusus dan sembunyikan lainnya
            if (info.disease_name.equals("Daun Sehat", ignoreCase = true)) {
                tvScientificName?.visibility = TextView.GONE
                tvDescription?.visibility = TextView.GONE
                tvCauses?.visibility = TextView.GONE
                tvTreatment?.visibility = TextView.GONE

                tvSymptoms?.text = "Daun dalam kondisi sehat, tidak ditemukan gejala penyakit."
            } else {
                // Fungsi bantu untuk menyembunyikan TextView jika kosong atau "-"
                fun setOrHide(textView: TextView?, value: String?) {
                    if (value.isNullOrBlank() || value == "-") {
                        textView?.visibility = TextView.GONE
                    } else {
                        textView?.visibility = TextView.VISIBLE
                        textView?.text = value
                    }
                }

                setOrHide(tvScientificName, info.scientific_name)
                setOrHide(tvDescription, info.description)
                setOrHide(tvSymptoms, info.symptoms)
                setOrHide(tvCauses, info.causes)
                setOrHide(tvTreatment, info.treatment)
            }

            setOnDismissListener {
                isCameraFrozen = false
            }

            show()
        }
    }


    override fun onResume() {
        super.onResume()
        isActivityActive = true
        predictionBuffer.clear()
        isCameraFrozen = false
        binding.resultText.text = "Mendeteksi..."
        binding.btnAnalyze.isEnabled = false
    }

    override fun onPause() {
        super.onPause()
        isActivityActive = false
    }

    override fun onStop() {
        super.onStop()
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }
}