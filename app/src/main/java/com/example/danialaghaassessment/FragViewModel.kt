package com.example.danialaghaassessment

import android.app.Application
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.OverlayItem


class FragViewModel(app:Application): AndroidViewModel(app) {
    var longitude = 0.0
    var latitude = 0.0
    var permission = false

    // Get a reference to the database, using the Application object
    var db = POIDatabase.getDatabase(app)
    var pois : LiveData<List<POI>>

    init{
        // When we initialise the ViewModel, get the LiveData from the DAO
        // The variable 'students' will always contain the latest LiveData.
        pois = db.poiDAO().getAllPois()
    }

    // Return the LiveData, so it can be observed, e.g. from the MainActivity
    fun getAllPOIS(): LiveData<List<POI>>{
        return pois
    }

    fun addPOI(newPOI: POI){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.poiDAO().insert(newPOI)
            }
        }
    }
}