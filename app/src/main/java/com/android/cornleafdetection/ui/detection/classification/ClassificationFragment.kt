package com.android.cornleafdetection.ui.detection.classification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.android.cornleafdetection.R
import com.android.cornleafdetection.databinding.FragmentClassificationBinding
import com.android.cornleafdetection.helper.Classifier
import com.android.cornleafdetection.ui.createCustomTempFile
import com.android.cornleafdetection.ui.detection.objectdetection.RealtimeActivity
import com.android.cornleafdetection.ui.uriToFile
import com.google.gson.Gson
import java.io.File

class ClassificationFragment : Fragment() {

    private var _binding: FragmentClassificationBinding? = null
    private val binding get() = _binding!!

    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private lateinit var classifier: Classifier
    private lateinit var mBitmap: Bitmap
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            title = "Deteksi Penyakit Jagung"
        }

        classifier = Classifier(requireContext())

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                showToast("Akses kamera ditolak.")
            }
        }

        checkCameraPermission()

        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnKamera.setOnClickListener { openCamera() }

        binding.btnRealtime2.setOnClickListener {
            startActivity(Intent(requireContext(), RealtimeActivity::class.java))
        }

        binding.btnAnalyze.setOnClickListener {
            getFile?.let { file ->
                navigateToResult(file)
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoUri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.fileprovider", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
            }
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = it.data?.data
                uri?.let { selectedUri ->
                    val file = uriToFile(selectedUri, requireContext())
                    getFile = file
                    binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }

    private fun navigateToResult(file: File) {
        try {
            mBitmap = BitmapFactory.decodeFile(file.path)
            val (diseaseName, confidence) = classifier.classifyImage(mBitmap)
            val dataDisease = classifier.getDiseaseInfoByName(diseaseName)

            val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                putExtra(ResultActivity.RESULT_IMAGE, getFile?.absolutePath)
                putExtra(ResultActivity.RESULT_TITLE, diseaseName)
                putExtra(ResultActivity.RESULT_CONFIDENCE, confidence)
                putExtra(ResultActivity.RESULT_DISEASE_DATA, Gson().toJson(dataDisease))
            }

            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            showToast(getString(R.string.error_loading_image))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        classifier.close()
        _binding = null
    }

    companion object {
        private const val TAG = "ClassificationFragment"
    }
}
