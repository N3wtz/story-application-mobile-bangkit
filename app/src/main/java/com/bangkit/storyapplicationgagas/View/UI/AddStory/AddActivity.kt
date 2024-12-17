package com.bangkit.storyapplicationgagas.View.UI.AddStory

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.Utils.reduceFileImage
import com.bangkit.storyapplicationgagas.Utils.uriToFile
import com.bangkit.storyapplicationgagas.View.UI.AddStory.CameraActivity.Companion.CAMERAX_RESULT
import com.bangkit.storyapplicationgagas.View.UI.MainActivity
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.bangkit.storyapplicationgagas.databinding.ActivityAddBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private val viewModel by viewModels<AddViewModel> { ViewModelFactory.getInstance(this) }
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationString: String? = null

    private val requestPermissLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        val message = if (isGranted) getString(R.string.permission_request_granted) else getString(R.string.permission_request_denied)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            requestPermissLauncher.launch(REQUIRED_PERMISSION)
        }

        observePostResult()

        binding.addImage.setOnClickListener { selectImage() }
        binding.btnPost.setOnClickListener { uploadImage() }

        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fetchLocation()
            } else {
                locationString = null // Reset jika switch dimatikan
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun observePostResult() {
        viewModel.postResult.observe(this) { result ->
            when (result) {
                is Response.Loading -> showLoadingState()
                is Response.Success -> showSuccessState()
                is Response.Error -> showErrorState()
            }
        }
    }

    private fun showLoadingState() {
        binding.lineProgressBar.apply {
            isIndeterminate = true
            visibility = View.VISIBLE
        }
        binding.overlayLoading.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showSuccessState() {
        binding.lineProgressBar.visibility = View.GONE
        binding.overlayLoading.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorState() {
        binding.overlayLoading.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.lineProgressBar.visibility = View.GONE
        Toast.makeText(this, "Upload Gagal", Toast.LENGTH_SHORT).show()
    }

    private fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    locationString = "${location.latitude},${location.longitude}"
                    Toast.makeText(this, "Location: $locationString", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.descriptionInput.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

            // Cek apakah lokasi tersedia dan parsing menjadi lat & lon
            val latPart: RequestBody? = locationString?.split(",")?.getOrNull(0)?.toRequestBody("text/plain".toMediaType())
            val lonPart: RequestBody? = locationString?.split(",")?.getOrNull(1)?.toRequestBody("text/plain".toMediaType())

            // Kirimkan ke ViewModel
            viewModel.postStory(multipartBody, requestBody, latPart, lonPart)
        } ?: run {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }




    private fun selectImage() {
        val optionActions = arrayOf<CharSequence>("Take Foto", "Your Galery", "Cancel")
        AlertDialog.Builder(this).apply {
            setTitle("Take Foto From")
            setIcon(R.mipmap.ic_launcher)
            setItems(optionActions) { dialog, i ->
                when (i) {
                    0 -> startCameraX()
                    1 -> startGallery()
                    2 -> dialog.dismiss()
                }
            }
        }.show()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            showImage()
        } ?: Log.d("Photo Picker", "No media selected")
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.addImage.setImageURI(it)
        }
        binding.tvAddPhoto.visibility = View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
