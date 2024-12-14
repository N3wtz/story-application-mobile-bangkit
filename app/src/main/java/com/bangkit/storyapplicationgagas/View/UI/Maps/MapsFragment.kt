package com.bangkit.storyapplicationgagas.View.UI.Maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.View.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.MapStyleOptions

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: ViewModelFactory

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        setMapStyle()
        getMyLocation()

        mapViewModel.stories.observe(viewLifecycleOwner) { data ->
            val boundsBuilder = LatLngBounds.Builder()  // Membuat builder untuk LatLngBounds

            data.forEach { story ->
                val latLng = LatLng(
                    story.lat ?: 0.0,  // Koordinat lat jika tidak null, default 0.0
                    story.lon ?: 0.0   // Koordinat lon jika tidak null, default 0.0
                )
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                boundsBuilder.include(latLng)  // Tambahkan setiap LatLng ke builder
            }

            // Membuat LatLngBounds dari builder
            val bounds = boundsBuilder.build()

            // Menentukan padding agar peta tidak terlalu terjepit
            val padding = 100
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)

            // Memindahkan kamera untuk mencakup semua marker
            mMap.moveCamera(cameraUpdate)
        }

        mapViewModel.getStoriesWithLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewModelFactory = ViewModelFactory.getInstance(requireContext())
        mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Log.e(TAG, "Location permission denied.")
            }
        }

    companion object {
        private const val TAG = "MapsFragment"
    }
}
