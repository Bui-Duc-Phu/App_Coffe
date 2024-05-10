package com.example.giuaky1.Administrator.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import com.example.giuaky1.Administrator.Activitys.MapsActivity
import com.example.giuaky1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject
import java.util.Locale

class Setting : Fragment(), OnMapReadyCallback {

    private var etAddress: EditText? = null
    private var searchAddress: SearchView? = null
    private var save: Button? = null
    private var database = FirebaseDatabase.getInstance().getReference("Addresses")
    private lateinit var mMap: GoogleMap
    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener { latLng ->
            val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
            val addresses: MutableList<Address>? =
                geocoder?.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val selectedAddress = addresses?.get(0)?.getAddressLine(0) ?: ""
            mMap.addMarker(MarkerOptions().position(latLng).title(selectedAddress))
        }
        mMap.setOnMarkerClickListener { marker ->
            val latLng = marker.position
            etAddress?.setText(marker.title)
         //   etAddress?.setText("${latLng.latitude}, ${latLng.longitude}")
            true
        }
    }
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_setting, container, false)
        etAddress = view.findViewById(R.id.etAddress)
        searchAddress = view.findViewById(R.id.searchAddress)
        loadDiaChiTuFireBase()
        save = view.findViewById(R.id.Save)
        etAddress!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (etAddress!!.right - etAddress!!.compoundDrawables[2].bounds.width())) {
                    val intent = Intent(context, MapsActivity::class.java)
                    startActivityForResult(intent, 1)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        save!!.setOnClickListener {
            val address = etAddress?.text.toString()
            database.setValue(address)
            Toast.makeText(context, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        searchAddress!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val client = OkHttpClient()
                        val url = "https://api.openrouteservice.org/geocode/autocomplete?api_key=5b3ce3597851110001cf6248d606a4ea83bd43af9ccc19dcc766408e&text=$newText"
                        val request = Request.Builder()
                            .url(url)
                            .build()

                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            val responseBody = response.body?.string()
                            val responseJson = JSONObject(responseBody)

                            // Extract latitude and longitude from response
                            val features = responseJson.getJSONArray("features")
                            if (features.length() > 0) {
                                val coordinates = features.getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONArray("coordinates")

                                val latLng = LatLng(coordinates.getDouble(1), coordinates.getDouble(0))

                                // Update map on UI thread
                                withContext(Dispatchers.Main) {
                                    mMap.addMarker(MarkerOptions().position(latLng).title(newText))
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                }
                            } else {
                                // Handle case when no features are returned in the response
                            }
                        }
                    }
                }
                return false
            }
        })
        return view
    }

    private fun loadDiaChiTuFireBase() {
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val address = it.value.toString()
                etAddress?.setText(address)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val selectedAddress = data?.getStringExtra("selectedAddress")
            etAddress?.setText(selectedAddress)
        }
    }
}