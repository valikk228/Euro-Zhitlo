package com.example.euro_zhitlo.Apartment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Landlord.LandlordMainActivity
import com.example.euro_zhitlo.Location.Country
import com.example.euro_zhitlo.Location.GeographyApiHandler
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class AddApartmentActivity : AppCompatActivity() {

    private lateinit var title:String
    private var price:Int = 0
    private lateinit var description:String
    private lateinit var facilities:List<String>
    private val geographyApiHandler = GeographyApiHandler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_apartment_activity)

        val Title:EditText = findViewById(R.id.editTextTitle)
        val Price:EditText = findViewById(R.id.editTextPrice)
        val Location:EditText = findViewById(R.id.autoCompleteTextView)
        val Description:EditText = findViewById(R.id.editTextDescription)
        val Facilities:EditText = findViewById(R.id.editTextFacilities)

        var imageUrl: String = intent.getStringExtra("imageUrl") ?: ""

        val back: ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener(){
            finish()
        }

        val addApartment: Button = findViewById(R.id.buttonAdd)
        val apartmentData = ApartmentData()

        var countries: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        var listOfCountries: List<Country>

        var cities: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView2)
        val citiesAdapter = ArrayAdapter(
            this@AddApartmentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        cities.setAdapter(citiesAdapter)

        // Отримання європейських країн з API та встановлення їх у Spinner
        geographyApiHandler.getAllEuropeanCountries { result ->
            runOnUiThread {
                result?.let { countriesJson ->
                    val europeanCountries = geographyApiHandler.parseCountriesFromJson(countriesJson).toMutableList()
                    val gson = Gson()
                    listOfCountries = gson.fromJson(countriesJson, Array<Country>::class.java).toList()

                    europeanCountries.removeIf { it == "Russian Federation" }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, europeanCountries)
                    countries.setAdapter(adapter)

                    // Слухач для вибору країни зі списку
                    countries.setOnItemClickListener { _, _, _, _ ->
                        var selectedCountry = ""
                        for(country in listOfCountries){
                            if(country.name == countries.text.toString()){
                                selectedCountry = country.alpha2code
                            }
                        }


                        if (selectedCountry != null) {
                            // Отримання міст для вибраної країни
                            geographyApiHandler.getCitiesByCountry(selectedCountry) { result ->
                                runOnUiThread {
                                    result?.let { citiesJson ->
                                        val listCities = geographyApiHandler.parseCitiesFromJson(citiesJson).toMutableList()


                                        val citiesAdapter = ArrayAdapter(
                                            this@AddApartmentActivity,
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

        addApartment.setOnClickListener() {
            if (Title.text.toString() != null && Price.text.toString() != null && Location.text.toString() != null
                && Description.text.toString() != null && Facilities.text.toString() != null
            ) {
                title = Title.text.toString()
                price = Price.text.toString().toInt()
                description = Description.text.toString()
                facilities = Facilities.text.toString().split(",")

                // Отримайте uid користувача з Firebase Authentication
                val currentUser = FirebaseAuth.getInstance().currentUser
                val uid = currentUser?.uid

                if (uid != null) {
                    apartmentData.uploadData(
                        uid,
                        imageUrl,
                        title,
                        price,
                        countries.text.toString(),
                        cities.text.toString(),
                        true,
                        description,
                        facilities
                    )
                    val intent = Intent(this@AddApartmentActivity,LandlordMainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // Обробка ситуації, коли uid користувача не знайдено
                }

            }
        }
    }
}