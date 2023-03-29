package com.example.danialaghaassessment

import android.Manifest
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
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem

class MainActivity : AppCompatActivity(), LocationListener{
lateinit var map1: MapView
lateinit var overlay_items : ItemizedIconOverlay<OverlayItem>
var longitutde = 0.0
var latitude = 0.0


    val addPOIlauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {


        val returnIntent: Intent? = it.data

        if(it.resultCode ==  RESULT_OK) {
            it.data?.apply {
                val name = this.getStringExtra("com.example.danialaghaassessment.Name")
                val type = this.getStringExtra("com.example.danialaghaassessment.Type")
                val description =  this.getStringExtra("com.example.danialaghaassessment.Description")

                val newPOI =  OverlayItem(name, "$type: $description", GeoPoint(latitude, longitutde))
                overlay_items.addItem(newPOI)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main)
        checkPermissions()

        map1 = findViewById<MapView>(R.id.map1)

        map1.controller.setZoom(14.0)
        map1.controller.setCenter(GeoPoint(50.9082, -1.4017))

        overlay_items =  ItemizedIconOverlay(this, arrayListOf<OverlayItem>(), null)
        map1.overlays.add(overlay_items)

        // give the map a default location and zoom of 14

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.addPOI -> {

                val intent = Intent(this, CreatePOIActivity::class.java)

                addPOIlauncher.launch(intent)
                return true
            }
        }
        return false
    }

    fun checkPermissions(){
        // Check to see if GPS permission has been granted already
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_GRANTED){
            requestLocation()
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
            val mgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions:Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestLocation()
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

        latitude = newLoc.latitude
        longitutde = newLoc.longitude

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
}