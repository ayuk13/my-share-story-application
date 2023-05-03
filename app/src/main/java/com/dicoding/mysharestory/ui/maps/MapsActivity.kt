package com.dicoding.mysharestory.ui.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.mysharestory.R
import com.dicoding.mysharestory.databinding.ActivityMapsBinding
import com.dicoding.mysharestory.ui.story.StoryActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity() ,
    OnMapReadyCallback {

    private val viewModel: MapsViewModel by viewModels()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var dataIntent: String? = null
    private var locationIntent: LatLng? = null
    private var markerLocation: Marker? = null
    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        dataIntent = intent.getStringExtra(INTENT_DATA)
        locationIntent = intent.getParcelableExtra(LOCATION_DATA)

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }

            dataIntent?.let {
                btnLocation.visibility = View.VISIBLE
                btnAddLocation.visibility = View.VISIBLE

                btnLocation.setOnClickListener { getMyLastLocation() }

                btnAddLocation.setOnClickListener {
                    selectedLocation?.let { latLng ->
                        Intent(this@MapsActivity, StoryActivity::class.java).apply {
                            putExtra(SET_LOCATION_DATA, latLng)
                            setResult(resultCode, this)
                        }
                        finish()
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()

        lifecycleScope.launch {
            viewModel.getMapStoryList()
        }

        observeLiveData()

        mMap.setOnMapClickListener {
            if (dataIntent != null) {
                markLocation(it)
            }
        }

        locationIntent?.let { latLng ->
            animateCamera(latLng)
            dataIntent?.let { markLocation(latLng) }
        }
    }

    private fun markLocation(location: LatLng) {
        markerLocation = if (markerLocation != null) {
            markerLocation?.remove()
            mMap.addMarker(MarkerOptions().position(location))
        } else {
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Marked")
            )
        }
        selectedLocation = location
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    animateCamera(
                        LatLng(location.latitude, location.longitude)
                    )

                    if (locationIntent != null || dataIntent != null) {
                        markLocation(LatLng(location.latitude, location.longitude))
                    }
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Location is not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun animateCamera(latLng: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        )
    }

    private fun observeLiveData() {
        viewModel.storyList.observe(this) { stories ->
            stories?.let {
                it.forEach { userstory ->
                    if (userstory.lat != null && userstory.lon != null) {
                        val location = LatLng(userstory.lat, userstory.lon)

                        mMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(userstory.name)
                                .snippet(userstory.description)
                        )
                    }
                }

                if (locationIntent == null) {
                    getMyLastLocation()
                }
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {

                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {

                getMyLastLocation()
            }
            else -> {

            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        const val INTENT_DATA = "Intent Data"
        const val LOCATION_DATA = "Location Intent Data"
        const val SET_LOCATION_DATA = "Set Location Data"
        const val resultCode = 202
    }
}