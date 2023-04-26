package com.example.danialaghaassessment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class FragMap: androidx.fragment.app.Fragment(R.layout.frag_map_view) {
    val viewModel: FragViewModel by activityViewModels()
    lateinit var map1: MapView
    lateinit var overlay_items : ItemizedIconOverlay<OverlayItem>
    var longitude = 0.0
    var latitude = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        map1 = view.findViewById<MapView>(R.id.map1)

        map1.controller.setZoom(14.0)
        map1.controller.setCenter(GeoPoint(50.9082, -1.4017))

        // Setting the map to have a default location if GPS permission has been denied by the user.
        // Also setting a default zoom of 14 to go with the default gps location.

        //Add this functionality later to add the POI Markers
        overlay_items =  ItemizedIconOverlay(activity, arrayListOf<OverlayItem>(), null)
        map1.overlays.add(overlay_items)
    }

//    override fun onLocationChanged(newLoc: Location) {
//        // Rename the map id to the frag id.
//        //val map1 = findViewById<MapView>(R.id.map1)
//        map1.controller.setZoom(14.0)
//        map1.controller.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude))
//
//
//        viewModel.latitude = newLoc.latitude
//        viewModel.longitude = newLoc.longitude
//
//        Toast.makeText (activity, "Location=${newLoc.latitude},${newLoc.longitude}", Toast.LENGTH_LONG).show()
//    }
//
//    override fun onProviderDisabled(provider: String) {
//        Toast.makeText (activity, "Provider disabled", Toast.LENGTH_LONG).show()
//    }
//
//    override fun onProviderEnabled(provider: String) {
//        Toast.makeText (activity, "Provider enabled", Toast.LENGTH_LONG).show()
//    }
//
//    // Deprecated at API level 29, but must still be included, otherwise your
//    // app will crash on lower-API devices as their API will try and call it
//    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
//
//    }

}