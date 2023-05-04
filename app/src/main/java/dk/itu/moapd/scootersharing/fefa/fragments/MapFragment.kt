/*
*MIT License
*
*Copyright (c) [2023] [Felix Jeppe Fatum]
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*/

package dk.itu.moapd.scootersharing.fefa.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import dk.itu.moapd.scootersharing.fefa.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.scootersharing.fefa.databinding.FragmentMapBinding
import dk.itu.moapd.scootersharing.fefa.models.RidesDB


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /** Initializes the RidesDB database and the CustomAdapter for the scooter list
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        ridesDB = RidesDB.get(this.requireContext())
        super.onCreate(savedInstanceState)
    }

    /**
     * Inflates the fragment layout and returns the root view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    /**
     * Sets up the UI elements and their click listeners using the binding object
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapFragment = binding.googleMaps.getFragment() as SupportMapFragment
        mapFragment.getMapAsync(this)
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            settingsButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, ProfileSettingsFragment()).commit()
            }
            ridesButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, ListRidesFragment()).commit()
            }
            homeButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, MainFragment()).commit()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 18f))
            }
        }
        addScooters(googleMap)
    }

    private fun addScooters(googleMap: GoogleMap){
        for(scooter in ridesDB.getRidesList()){
            if(!scooter.inUse) {
                val coordinates = LatLng(scooter.lat, scooter.lon)
                val markerOptions = MarkerOptions()
                    .position(coordinates)
                    .title(scooter.id)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.scooter_svgrepo_com)))
                googleMap.addMarker(markerOptions)
            }
        }
        googleMap.setOnMarkerClickListener(OnMarkerClickListener { marker ->
            val dialog = ScooterDialog()
            val bundle = Bundle()
            bundle.putString("scooterID", marker.title)
            dialog.arguments = bundle
            dialog.show(parentFragmentManager, "scooterDialog")
            return@OnMarkerClickListener true
        })
    }

    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
        val drawable = context?.let { ContextCompat.getDrawable(it, drawableId) }
            ?: throw IllegalArgumentException("Drawable not found")
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        lateinit var ridesDB: RidesDB
    }
}