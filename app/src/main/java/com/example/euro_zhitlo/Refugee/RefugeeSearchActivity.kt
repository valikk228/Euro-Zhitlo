package com.example.euro_zhitlo.Refugee

import Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.euro_zhitlo.Location.City
import com.example.euro_zhitlo.Location.Country
import com.example.euro_zhitlo.Location.GeographyApiHandler
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class RefugeeSearchActivity : AppCompatActivity() {

    private val navigation = Navigation(this)
    lateinit var countries: AutoCompleteTextView
    private val geographyApiHandler = GeographyApiHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_search_activity)

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView, 1)

        countries = findViewById(R.id.autoCompleteTextView)
        val price: EditText = findViewById(R.id.editTextNumber)
        val availiable: CheckBox = findViewById(R.id.checkAvailiable)
        val search: Button = findViewById(R.id.buttonSearch)

        var listOfCountries: List<Country>? = null

        geographyApiHandler.getAllEuropeanCountries { result ->
            runOnUiThread {
                result?.let { countriesJson ->
                    val europeanCountries =
                        geographyApiHandler.parseCountriesFromJson(countriesJson)
                            .toMutableList()
                    val gson = Gson()
                    listOfCountries =
                        gson.fromJson(countriesJson, Array<Country>::class.java).toList()

                    europeanCountries.removeIf { it == "Russian Federation" }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        europeanCountries
                    )
                    countries.setAdapter(adapter)
                    }
                }
            }


        search.setOnClickListener(){
            val intent = Intent(this@RefugeeSearchActivity,AdvancedSearchActivity::class.java)
            val extras = Bundle()
            extras.putString("country", countries.text.toString())
            extras.putString("price",price.text.toString())
            extras.putBoolean("availiable", availiable.isChecked)
            intent.putExtras(extras)
            startActivity(intent)
        }
    }
}

