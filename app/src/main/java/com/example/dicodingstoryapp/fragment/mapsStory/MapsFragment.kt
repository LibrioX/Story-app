package com.example.dicodingstoryapp.fragment.mapsStory

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstoryapp.R
import com.example.dicodingstoryapp.adapter.StoryAdapterLocation
import com.example.dicodingstoryapp.data.api.model.ListStoryItem
import com.example.dicodingstoryapp.databinding.FragmentMapsBinding
import com.example.dicodingstoryapp.preferences.TokenViewModel
import com.example.dicodingstoryapp.preferences.UserPreferences
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.viewModelFactory.PreferencesViewModelFactory
import com.example.dicodingstoryapp.viewModelFactory.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var sheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true

        getMyLocation()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout.root)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        val tokenViewModel = ViewModelProvider(
            this,
            PreferencesViewModelFactory(pref)
        )[TokenViewModel::class.java]

        tokenViewModel.getToken().observe(viewLifecycleOwner) {
            mapsViewModel.setToken(it)
            mapsViewModel.getStoriesWithLocation()
        }

        mapsViewModel.storiesResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    if (it.data.listStory.isNotEmpty()) {
                        setUsersData(it.data.listStory)
                    } else {
                        toastMaker(resources.getString(R.string.noStories))
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }

                else -> {}
            }
        }

        val density = resources.displayMetrics.density

        binding.btnShowBottomSheet.setOnClickListener {
            sheetBehavior?.peekHeight = (40 * density).toInt()
            binding.btnShowBottomSheet.visibility = View.INVISIBLE
        }

        binding.bottomSheetLayout.btnHideBottomSheet.setOnClickListener {

            sheetBehavior?.peekHeight = 0
            binding.btnShowBottomSheet.visibility = View.VISIBLE

        }

        sheetBehavior?.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("MapsFragment", "onStateChanged: $newState")
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.bottomSheetLayout.btnHideBottomSheet.visibility = View.VISIBLE
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    binding.bottomSheetLayout.btnHideBottomSheet.visibility = View.INVISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

    }

    private fun showLoading(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setUsersData(users: List<ListStoryItem>) {

        val adapter = StoryAdapterLocation(users, onClick = {
            Log.d("MapsFragment", "setUsersData: ${it.lat} ${it.lon}")
            if (it.lat != null && it.lon != null) {

                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.lat.toDouble(),
                            it.lon.toDouble()
                        ), 15f
                    )
                )
                sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
        binding.bottomSheetLayout.rvStoriesLocation.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.bottomSheetLayout.rvStoriesLocation.adapter = adapter

        users.forEach {
            if (it.lat != null && it.lon != null) {
                val latLng = LatLng(it.lat.toDouble(), it.lon.toDouble())
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(it.name)
                        .snippet(it.description)
                )
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }


    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)


        }
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}