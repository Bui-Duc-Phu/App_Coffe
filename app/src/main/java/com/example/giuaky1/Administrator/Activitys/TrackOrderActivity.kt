package com.example.giuaky1.Administrator.Activitys

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject
import org.osmdroid.util.BoundingBox
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class TrackOrderActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var userAddress: String? = null
    private var shopAddress: String? = null
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0
    private var shopLatitude: Double = 0.0
    private var shopLongitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)

        Configuration.getInstance()
            .load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        mapView = findViewById(R.id.map)
        mapView.setMultiTouchControls(true)
        userAddress = intent.getStringExtra("userAddress")
        shopAddress = intent.getStringExtra("shopAddress")
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val addresses1: List<Address>?

        try {
            addresses = userAddress?.let { geocoder.getFromLocationName(it, 1) }
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                userLatitude = address.latitude
                userLongitude = address.longitude
            } else {
                Log.d("TrackOrderActivity123", "Unable to find coordinates for address: $userAddress")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            addresses1 = shopAddress?.let { geocoder.getFromLocationName(it, 1) }
            if (addresses1 != null && addresses1.isNotEmpty()) {
                val address = addresses1[0]
                shopLatitude = address.latitude
               shopLongitude = address.longitude
                Log.d("TrackOrderActivity123", "Shop coordinates: $shopLatitude, $shopLongitude")
            } else {
                Log.d("TrackOrderActivity123", "Unable to find coordinates for address: $userAddress")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        getDirections()
    }

    private fun zoomRoute(lstGeoPointRoute: List<GeoPoint>) {
        if (lstGeoPointRoute.isNotEmpty()) {
            val polyline = Polyline()
            polyline.setPoints(lstGeoPointRoute)
            mapView.overlays.add(polyline)

            // Create a BoundingBox that includes all points on the route
            val boundingBox = BoundingBox.fromGeoPoints(lstGeoPointRoute)

            // Zoom to the BoundingBox
            mapView.zoomToBoundingBox(boundingBox, true)
        }
    }

    private fun animateMarker(path: ArrayList<GeoPoint>) {
        val marker = Marker(mapView)
        marker.position = path.first()
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        val valueAnimator = ValueAnimator.ofInt(0, path.size - 1) // Change here
        valueAnimator.addUpdateListener { animation ->
            val position = animation.animatedValue as Int
            marker.position = path[position]
            mapView.invalidate()
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Animation ended, remove the marker
                mapView.overlays.remove(marker)
                mapView.invalidate()
                finish()
            }
        })

        valueAnimator.duration = 200000 // Set duration of the animation
        valueAnimator.start()
    }

    private fun getDirections() {
    CoroutineScope(Dispatchers.IO).launch {
        val destination = "$userLongitude,$userLatitude"
        val origin = "$shopLongitude,$shopLatitude"

        val urlString = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248d606a4ea83bd43af9ccc19dcc766408e&start=$origin&end=$destination"

        val request = Request.Builder()
            .url(urlString)
            .addHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
            .build()

        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string()
            val responseJson = JSONObject(responseBody)

            if (!responseJson.has("features")) {
                // The "features" key does not exist in the JSON response
                // Handle the error here
                return@launch
            }

            val routes = responseJson.getJSONArray("features")
            val route = routes.getJSONObject(0)
            val geometry = route.getJSONObject("geometry")
            val coordinates = geometry.getJSONArray("coordinates")

            val path = ArrayList<GeoPoint>()
            for (i in 0 until coordinates.length()) {
                val coordinate = coordinates.getJSONArray(i)
                val longitude = coordinate.getDouble(0)
                val latitude = coordinate.getDouble(1)
                path.add(GeoPoint(latitude, longitude))
            }

            // Switch to the Main (UI) dispatcher to update the UI
            withContext(Dispatchers.Main) {
                animateMarker(path)
                zoomRoute(path)

                // Add markers for the start and end points
                val startMarker = Marker(mapView)
                startMarker.position = path.first()
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                startMarker.title = "CoffeHub"
                mapView.overlays.add(startMarker)

                val endMarker = Marker(mapView)
                endMarker.position = path.last()
                endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                endMarker.title = "Địa chỉ của bạn"
                mapView.overlays.add(endMarker)

                // Refresh the map
                mapView.invalidate()
            }
        }
    }
}
}