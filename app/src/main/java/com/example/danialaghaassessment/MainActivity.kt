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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.fuel.json.responseJson


class MainActivity : AppCompatActivity(), LocationListener{
    lateinit var map1: MapView
    lateinit var overlay_items : ItemizedIconOverlay<OverlayItem>
    var poi_List = ArrayList<POI>()
    var webPOIList = ArrayList<POI>()
    var longitutde = 0.0
    var latitude = 0.0
    var checkbox = false

    val addPOIlauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        val returnIntent: Intent? = it.data

        if(it.resultCode ==  RESULT_OK) {
            it.data?.apply {
                val name = this.getStringExtra("com.example.danialaghaassessment.Name").toString()
                val type = this.getStringExtra("com.example.danialaghaassessment.Type").toString()
                val description =  this.getStringExtra("com.example.danialaghaassessment.Description").toString()

                if(name.isNullOrEmpty() || type.isNullOrEmpty() || description.isNullOrEmpty()) {
                    Toast.makeText(this@MainActivity, "POI not created. Please fill in all fields to create a new POI.", Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }

                val newPOI =  OverlayItem(name, "$type: $description", GeoPoint(latitude, longitutde))
                overlay_items.addItem(newPOI)

                val newPOIObject = POI(0, name, type, description, latitude, longitutde)
                poi_List.add(newPOIObject)

                if(checkbox == true ){
                    val url = "http://10.0.2.2:3000/poi/create"
                    val createPOIPost = listOf("name" to name, "type" to type, "description" to description, "lat" to newPOIObject.latitude, "lon" to newPOIObject.longitude)
                    url.httpPost(createPOIPost).response{ request, response, result ->
                        when(result){
                            is Result.Success -> {
                                // If the POST request is successful
                                Toast.makeText(this@MainActivity, result.get().decodeToString() + "Uploaded successfully to web", Toast.LENGTH_LONG).show()
                            }
                            is Result.Failure -> {
                                // If the POST request failed
                                Toast.makeText(this@MainActivity, result.error.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
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

        // Setting the map to have a default location if GPS permission has been denied by the user.
        // Also setting a default zoom of 14 to go with the default gps location.

        val markerGestureListener = object:ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
            override fun onItemLongPress(i: Int, item: OverlayItem) : Boolean
            {
                Toast.makeText(this@MainActivity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
            override fun onItemSingleTapUp(i:Int, item: OverlayItem): Boolean
            {
                Toast.makeText(this@MainActivity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }
        }

        overlay_items =  ItemizedIconOverlay(this, arrayListOf<OverlayItem>(), markerGestureListener)
        map1.overlays.add(overlay_items)
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

            R.id.saveAllPOIs -> {
                val db  = POIDatabase.getDatabase(application)
                for (poi in poi_List) {
                    // Launch the coroutine in the scope of the activity lifecycle
                    // (when the activity is destroyed, the coroutine will terminate
                    // as the activity is its parent)

                    lifecycleScope.launch{
                        // Read in the POI Details from each POI object within the poi_list.
                        val name = poi.name
                        val type = poi.type
                        val description = poi.description
                        val latitude = poi.latitude
                        val longitude = poi.longitude

                        // Declare a variable to hold the ID allocated to the new record
                        var insertId = 0L

                        // Switch to the background to do the insert query

                        withContext(Dispatchers.IO){
                            val savedPOI = POI(0, name, type, description, latitude, longitude)

                            insertId = db.poiDAO().insert(savedPOI)
                        }
                    }
                    
                    //Log message that I used to debug my code when tackling Task 3
                    //Log.d("Point Of Interests:", "${poi}")
                }
                poi_List.clear()
            }
            R.id.preferences -> {
                // Launch the preferences activity

                val intent = Intent(this, MyPrefsActivity::class.java)

                startActivity(intent)

                return true
            }
            R.id.loadPOIs -> {
                // Clear the unsaved POIs in the POIs List before loading locally saved POIs
                poi_List.clear()

                //clear any markers that might have been loaded from the web to show only local markers
                overlay_items.removeAllItems()

                val db = POIDatabase.getDatabase(application)
                lifecycleScope.launch {

                    var savedPOIs = listOf<POI>()

                    withContext(Dispatchers.IO){
                        // Retrieve all Point of Interests from local SQL Database
                        savedPOIs =  db.poiDAO().getAllPois()
                    }

                    for(savedPOI in savedPOIs){
                        val name = savedPOI.name
                        val type = savedPOI.type
                        val description = savedPOI.description
                        val latitude = savedPOI.latitude
                        val longitude = savedPOI.longitude

                        val tempSavedPOI = OverlayItem(name, type, description, GeoPoint(latitude, longitude))
                        overlay_items.addItem(tempSavedPOI)
                    }
                    map1.invalidate()
                }
            }

            R.id.loadWebPOIs -> {
                // Clear the unsaved POIs in the POIs List before loading locally saved POIs
                poi_List.clear()

                //clear any markers that might have been loaded locally to show only web saved markers
                overlay_items.removeAllItems()

                val url = "http://10.0.2.2:3000/poi/all"

                url.httpGet().responseJson { request, response, result ->
                    when(result) {
                        is Result.Success -> {
                            val jsonArray = result.get().array()
                            var str = ""

                            for(i in 0 until jsonArray.length()){
                                val currentPOIObject = jsonArray.getJSONObject(i)
                                val name =  currentPOIObject.getString("name")
                                val type =  currentPOIObject.getString("type")
                                val description = currentPOIObject.getString("description")
                                val longitude = currentPOIObject.getDouble("lon")
                                val latitude = currentPOIObject.getDouble("lat")

                                val webPOI = POI(0, name, type, description, latitude, longitude)
                                webPOIList.add(webPOI)
                            }

                            for(webPOI in webPOIList){
                                val tempWebPOI = OverlayItem(webPOI.name, webPOI.type, webPOI.description, GeoPoint(webPOI.latitude, webPOI.longitude))
                                overlay_items.addItem(tempWebPOI)
                            }
                            map1.invalidate()
                        }

                        is Result.Failure -> {
                            Toast.makeText(this@MainActivity, result.error.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
            R.id.testGlobalCheckbox -> {
                if (checkbox) {
                    Toast.makeText(this, "Checkbox is checked", Toast.LENGTH_LONG).show()
                    Log.d("DEBUG_TAG", "Checkbox is true")
                } else {
                    Toast.makeText(this, "Checkbox is unchecked", Toast.LENGTH_LONG).show()
                    Log.d("DEBUG_TAG", "Checkbox is false")
                }
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

    override fun onStart(){
        super.onStart()
        Log.d("lifecycle_app", "onStart")
    }

    override fun onResume() {
        super.onResume()

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val checkBox = prefs.getBoolean("uploadPoisToWeb", false)

        if(checkBox == true){
            Log.d("lifecycle_app", "Checkbox Preference has been successfully implemented")
            checkbox = true
        }
        else if(checkBox == false){
            checkbox = false
        }
    }
    // To keep code concise I know I can just used an else to set the global checkbox to false. But
    // for better understanding and readability of my code I decided to use an else if statement.

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
