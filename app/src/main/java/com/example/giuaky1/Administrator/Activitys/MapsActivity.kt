package com.example.giuaky1.Administrator.Activitys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.giuaky1.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.giuaky1.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmButton.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("selectedAddress", selectedAddress)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        // Get the current location and set it as the selected address
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1000
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    selectedAddress = addresses?.get(0)?.getAddressLine(0) ?: ""
                    Toast.makeText(this@MapsActivity, "Địa chỉ hiện tại: $selectedAddress", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1000
            )
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.setOnMapClickListener { latLng ->
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            selectedAddress = addresses?.get(0)?.getAddressLine(0) ?: ""
            Toast.makeText(this@MapsActivity, "Địa chỉ đã chọn: $selectedAddress", Toast.LENGTH_LONG).show()

            // Clear all existing markers
            mMap.clear()

            // Add a new marker at the clicked location
            mMap.addMarker(MarkerOptions().position(latLng).title("Địa chỉ đã chọn"))
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("Marker in Current Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap)
            }
        }
    }
}