package com.example.giuaky1.Administrator.Activitys

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.giuaky1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.giuaky1.databinding.ActivityTrackOrderBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker

class TrackOrderActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityTrackOrderBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userAddress: String? = null
    private var shopAddress: String? = null
    private lateinit var shopMarker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        userAddress = intent.getStringExtra("userAddress")
        shopAddress = intent.getStringExtra("shopAddress")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val geocoder = Geocoder(this, Locale.getDefault())

        val userAddresses = userAddress?.let { geocoder.getFromLocationName(it, 1) }
        val shopAddresses = shopAddress?.let { geocoder.getFromLocationName(it, 1) }

        if (shopAddresses != null) {
            if (userAddresses != null) {
                if (userAddresses.isNotEmpty() && shopAddresses.isNotEmpty()) {
                    val userLatLng = LatLng(userAddresses[0].latitude, userAddresses[0].longitude)
                    val shopLatLng = LatLng(shopAddresses[0].latitude, shopAddresses[0].longitude)

                    mMap.addMarker(MarkerOptions().position(userLatLng).title("Địa chỉ của bạn"))
                    mMap.addMarker(MarkerOptions().position(shopLatLng).title("Địa chỉ của Shipper"))
                    shopMarker = mMap.addMarker(MarkerOptions().position(shopLatLng).title("Địa chỉ của Shipper"))!!
                    val builder = LatLngBounds.builder()
                    builder.include(userLatLng)
                    builder.include(shopLatLng)
                    val bounds = builder.build()

                    val padding = 100  // padding in pixels
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    mMap.moveCamera(cameraUpdate)
                    animateMarker(shopLatLng, userLatLng)
                }
            }
        }
    }
    private fun animateMarker(startLatLng: LatLng, endLatLng: LatLng) {
        val valueAnimator = ValueAnimator.ofObject(LatLngEvaluator(), startLatLng, endLatLng)
        valueAnimator.duration = 30000  // duration of the animation in milliseconds
        valueAnimator.addUpdateListener { animation ->
            val animatedLatLng = animation.animatedValue as LatLng
            shopMarker.position = animatedLatLng
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mMap.clear()  // Clear the map
                Toast.makeText(this@TrackOrderActivity, "Shipper đã đến", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
        valueAnimator.start()
    }
    private class LatLngEvaluator : TypeEvaluator<LatLng> {
        override fun evaluate(fraction: Float, startValue: LatLng, endValue: LatLng): LatLng {
            val latitude = startValue.latitude + ((endValue.latitude - startValue.latitude) * fraction)
            val longitude = startValue.longitude + ((endValue.longitude - startValue.longitude) * fraction)
            return LatLng(latitude, longitude)
        }
    }
}