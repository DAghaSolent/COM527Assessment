package com.example.danialaghaassessment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class FragMap: androidx.fragment.app.Fragment(R.layout.frag_map_view) {
    val viewModel: FragViewModel by activityViewModels()
    lateinit var map1: MapView
    lateinit var overlay_items : ItemizedIconOverlay<OverlayItem>

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

        viewModel.getAllPOIS().observe(this.viewLifecycleOwner, Observer {
            for(poi in it){
                val newPOI = OverlayItem(poi.name, "${poi.type}: ${poi.description}", GeoPoint(poi.latitude, poi.longitude))
                overlay_items.addItem(newPOI)
            }
        })

        val markerGestureListener = object:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
            override fun onItemLongPress(i: Int, item: OverlayItem) : Boolean
            {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
            override fun onItemSingleTapUp(i:Int, item: OverlayItem): Boolean
            {
                Toast.makeText(activity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        overlay_items =  ItemizedIconOverlay(activity, arrayListOf<OverlayItem>(), markerGestureListener)
        map1.overlays.add(overlay_items)
    }
}