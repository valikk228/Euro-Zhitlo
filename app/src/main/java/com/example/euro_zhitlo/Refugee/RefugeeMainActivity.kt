package com.example.euro_zhitlo.Refugee

import Apartment
import ApartmentAdapter
import Navigation
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentData
import com.example.euro_zhitlo.Location.City
import com.example.euro_zhitlo.Location.Country
import com.example.euro_zhitlo.Location.GeographyApiHandler
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class RefugeeMainActivity : AppCompatActivity() {

    private val navigation = Navigation(this)
    private val geographyApiHandler = GeographyApiHandler()
    val read = ApartmentData()
    lateinit var countries: AutoCompleteTextView
    lateinit var cities: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView, 0)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val search: Button = findViewById(R.id.button5)
        val cancel: Button = findViewById(R.id.buttonCancel)

        countries = findViewById(R.id.autoCompleteTextView)
        cities = findViewById(R.id.autoCompleteTextView2)
        val citiesAdapter = ArrayAdapter(
            this@RefugeeMainActivity,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        cities.setAdapter(citiesAdapter)

        var listOfCountries: List<Country>? = null
        var listOfCities: List<City>? = null

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

                    // Слухач для вибору країни зі списку
                    countries.setOnItemClickListener { _, _, _, _ ->
                        var selectedCountry = ""
                        for (country in listOfCountries!!) {
                            if (country.name == countries.text.toString()) {
                                selectedCountry = country.alpha2code
                            }
                        }

                        if (selectedCountry != null) {
                            // Отримання міст для вибраної країни
                            geographyApiHandler.getCitiesByCountry(selectedCountry) { result ->
                                runOnUiThread {
                                    result?.let { citiesJson ->
                                        val listCities =
                                            geographyApiHandler.parseCitiesFromJson(
                                                citiesJson
                                            ).toMutableList()
                                        val gson = Gson()
                                        listOfCities = gson.fromJson(
                                            citiesJson,
                                            Array<City>::class.java
                                        ).toList()


                                        val citiesAdapter = ArrayAdapter(
                                            this@RefugeeMainActivity,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            listCities
                                        )
                                        cities.setAdapter(citiesAdapter)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SetRecyclerView(cancel,search,recyclerView)

    }

    fun SetRecyclerView(cancel: Button, search:Button,recyclerView:RecyclerView){
        read.readApartmentsRefugeeData { apartments ->
            var adapter = ApartmentAdapter(this, apartments)
            adapter.setOnItemClickListener(object : ApartmentAdapter.OnItemClickListener {
                override fun onItemClick(apartment: Apartment) {
                    val intent = Intent(this@RefugeeMainActivity, ApartmentActivity::class.java)
                    val extras = Bundle()
                    extras.putParcelable("apartment", apartment)
                    intent.putExtras(extras)
                    startActivity(intent)
                }
            })
            recyclerView.adapter = adapter

            search.setOnClickListener {
                var filterApartments = ArrayList<Apartment>()
                val countryName: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
                val cityName: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView2)

                cancel.setOnClickListener(){
                    SetRecyclerView(cancel,search,recyclerView)
                    countries.setText("")
                    cities.setText("")
                }

                if (countryName.text.toString() != "" && cityName.text.toString() != "") {
                    for (apartment in apartments) {
                        if (apartment.country == countryName.text.toString() && apartment.city == cityName.text.toString()) {
                            filterApartments.add(apartment)
                        }
                    }
                } else if (countryName.text.toString() != "") {
                    for (apartment in apartments) {
                        if (apartment.country == countryName.text.toString()) {
                            filterApartments.add(apartment)
                        }
                    }
                }
                adapter = ApartmentAdapter(this, filterApartments)
                adapter.setOnItemClickListener(object : ApartmentAdapter.OnItemClickListener {
                    override fun onItemClick(apartment: Apartment) {
                        val intent = Intent(this@RefugeeMainActivity, ApartmentActivity::class.java)
                        val extras = Bundle()
                        extras.putParcelable("apartment", apartment)
                        intent.putExtras(extras)
                        startActivity(intent)
                    }
                })
                recyclerView.adapter = adapter
            }
        }
    }
}
