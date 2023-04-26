package com.example.danialaghaassessment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.fuel.json.responseJson


class MainActivity : AppCompatActivity(), LocationListener{
    val viewModel: FragViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db  = POIDatabase.getDatabase(application)
        checkPermissions()
    }

    fun checkPermissions(){
        // Check to see if GPS permission has been granted already
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED){
            requestLocation()
            viewModel.permission = true
        } else {
            //If the permission hasn't been granted yet, request it from the user.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    fun requestLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED){
            // note the use of 'as' to perform type casting in Kotlin
            // getSystemService() returns a superclass type of LocationManager,
            // so we need to cast it to LocationManager.
            val mgr = getSystemService(LOCATION_SERVICE) as LocationManager

            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions:Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestLocation()
                    viewModel.permission = true
                }
                else{
                    AlertDialog.Builder(this)
                        .setPositiveButton("OK", null) // add an OK button with an optional event handler
                        .setMessage("You have not accepted request Location.") // set the message
                        .show() // show the dialog
                }
            }
        }
    }

    override fun onLocationChanged(newLoc: Location) {
        val map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(14.0)
        map1.controller.setCenter(GeoPoint(newLoc.latitude, newLoc.longitude))

        viewModel.latitude = newLoc.latitude
        viewModel.longitude = newLoc.longitude

        Toast.makeText (this, "Location=${newLoc.latitude},${newLoc.longitude}", Toast.LENGTH_LONG).show()
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText (this, "Provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText (this, "Provider enabled", Toast.LENGTH_LONG).show()
    }

    // Deprecated at API level 29, but must still be included, otherwise your
    // app will crash on lower-API devices as their API will try and call it
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onStart(){
        super.onStart()
        Log.d("lifecycle_app", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle_app", "onResume")

    }

    override fun onStop(){
        super.onStop()
        Log.d("lifecycle_app", "onStop")
    }

    override fun onPause(){
        super.onPause()
        Log.d("lifecycle_app", "onPause")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.d("lifecycle_app", "onDestroy")
    }

}