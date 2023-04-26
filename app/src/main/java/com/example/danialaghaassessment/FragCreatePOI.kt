package com.example.danialaghaassessment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

class FragCreatePOI: androidx.fragment.app.Fragment(R.layout.frag_create_poi) {
    val viewModel : FragViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity));

        view.findViewById<Button>(R.id.addPOIbtn).setOnClickListener {
            val etname = view.findViewById<EditText>(R.id.etname)
            val getetname = etname.getText().toString()

            val ettype = view.findViewById<EditText>(R.id.ettype)
            val getettype = ettype.getText().toString()

            val etdescription = view.findViewById<EditText>(R.id.etdescripton)
            val getdescription = etdescription.getText().toString()

            if(viewModel.permission == true ){
                if(getetname.isNullOrEmpty() || getettype.isNullOrEmpty() || getdescription.isNullOrEmpty()) {
                    Toast.makeText(activity, "POI not created. Please fill in all fields to create a new POI.", Toast.LENGTH_LONG).show()
                }

                else{
                    var newPOI = POI(0,getetname, getettype, getdescription,viewModel.latitude, viewModel.longitude)

                    viewModel.addPOI(newPOI)
                }
            }
            else{
                Toast.makeText(activity, "POI not created. Re run the app and accept location permissions.", Toast.LENGTH_LONG).show()
            }
        }
    }
}