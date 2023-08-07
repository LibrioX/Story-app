package com.example.dicodingstoryapp.fragment.addStory

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.databinding.FragmentAddStoryBinding
import com.example.dicodingstoryapp.file.*
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory
import com.example.dicodingstoryapp.viewModelFactory.ViewModelFactory
import com.example.dicodingstoryapp.preferences.UserPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationUser: Location? = null

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)

            val bitmap = rotateBitmapCamera(myFile)
            binding.imgView.setImageBitmap(bitmap)
            getFile = bitmapToFile(bitmap, myFile)

        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireActivity())
            val bitmap = rotateBitmapCamera(myFile)

            binding.imgView.setImageURI(selectedImg)
            getFile = bitmapToFile(bitmap, myFile)
        }
    }

    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreferences.getInstance(requireContext().dataStore)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val tokenViewModel =
            ViewModelProvider(this, PreferencesViewModelFactory(pref))[TokenViewModel::class.java]
        tokenViewModel.getToken().observe(viewLifecycleOwner) {
            addStoryViewModel.setToken(it)
        }

        addStoryViewModel.postStoryResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    toastMaker(resources.getString(R.string.uploadSuccess))
                    goToHome()
                }

                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }

                else -> {}
            }
        }

        binding.apply {
            btnCamera.setOnClickListener {
                startTakePhoto()
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            buttonAdd.setOnClickListener {
                if (edAddDescription.text.toString().isNotEmpty()) {
                    preprocessingData()
                } else {
                    toastMaker(resources.getString(R.string.errorHandlingDesc))
                }

            }
            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLastLocation()
                } else {
                    locationUser = null
                }
            }
        }


    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)
        createTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                authorityName,
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.choosePhoto))
        launcherIntentGallery.launch(chooser)
    }

    private fun preprocessingData() {
        getFile?.let {
            val file = reduceFileImage(it)

            val description =
                binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val lat = locationUser?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
            val lon = locationUser?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.setFile(imageMultipart)
            addStoryViewModel.setDescription(description)
            if (lat != null && lon != null) {
                addStoryViewModel.setLat(lat)
                addStoryViewModel.setLon(lon)
            }

            addStoryViewModel.postStory()
        } ?: run {
            toastMaker(resources.getString(R.string.errorHandlingPhoto))
            return
        }


    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun goToHome() {
        view?.findNavController()?.navigate(R.id.action_addStoryFragment_to_homeFragment)
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locationUser = location
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val authorityName = "com.example.dicodingstoryapp"
    }


}