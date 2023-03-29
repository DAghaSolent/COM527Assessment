package com.example.danialaghaassessment

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import org.osmdroid.config.Configuration

class CreatePOIActivity:  AppCompatActivity() {
    override fun onCreate(SavedInstanceState: Bundle?){
        super.onCreate(SavedInstanceState)

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_create_poi)

        val createPOI = findViewById<Button>(R.id.addPOIbtn)

        createPOI.setOnClickListener {
            val etname = findViewById<EditText>(R.id.etname)
            val getetname = etname.getText().toString()

            val ettype =  findViewById<EditText>(R.id.ettype)
            val getettype = ettype.getText().toString()

            val etdescription = findViewById<EditText>(R.id.etdescripton)
            val getetdescription = etdescription.getText().toString()



            val intent = Intent()

            val bundle = bundleOf("com.example.danialaghaassessment.Name" to getetname,
                                        "com.example.danialaghaassessment.Type" to getettype,
                                        "com.example.danialaghaassessment.Description" to getetdescription)
            intent.putExtras(bundle)

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}