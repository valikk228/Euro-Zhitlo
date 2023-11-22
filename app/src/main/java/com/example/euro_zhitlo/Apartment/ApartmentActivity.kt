package com.example.euro_zhitlo.Apartment

import Apartment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.euro_zhitlo.Account.ProfileActivity
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.Chat.ChatActivity
import com.example.euro_zhitlo.Location.City
import com.example.euro_zhitlo.Location.Country
import com.example.euro_zhitlo.Location.GeographyApiHandler
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class ApartmentActivity: AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val userId = user?.uid
    private val geographyApiHandler = GeographyApiHandler()
    private var apartment = Apartment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apartment_activity)

        val back:ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener(){
            finish()
        }

        val extras = intent.extras

        if (extras != null) {
            apartment = extras.getParcelable<Apartment>("apartment")!!
        }


        var listOfCountries: List<Country>? = null
        var listOfCities: List<City>? = null
        var extraCountry = ""
        var extraCity = City("",0,0.0,0.0)
        val openMap:TextView = findViewById(R.id.textView3)
        val openLandlord:TextView = findViewById(R.id.textView4)

        openLandlord.setOnClickListener(){
            if (extras != null) {
                val intent = Intent(this@ApartmentActivity, ProfileActivity::class.java)
                User.getUserByUid(apartment.uid_poster){landlord->
                    intent.putExtra("landlord",landlord)
                    startActivity(intent)
                }
            }
        }


        openMap.setOnClickListener(){
            geographyApiHandler.getAllEuropeanCountries { result ->
                runOnUiThread {
                    result?.let { countriesJson ->

                        val gson = Gson()
                        listOfCountries =
                            gson.fromJson(countriesJson, Array<Country>::class.java).toList()

                        for(country in listOfCountries!!) {
                            if (apartment.country == country.name) {
                                extraCountry = country.alpha2code
                            }
                        }

                        geographyApiHandler.getCitiesByCountry(extraCountry) { result ->
                            runOnUiThread {
                                result?.let { citiesJson ->

                                    val gson = Gson()
                                    listOfCities = gson.fromJson(
                                        citiesJson,
                                        Array<City>::class.java
                                    ).toList()

                                    for(city in listOfCities!!){
                                        if(city.name == apartment.city){
                                            extraCity = city
                                        }
                                    }
                                    val latitude = extraCity.latitude
                                    val longitude = extraCity.longitude

                                    val uri = "geo:$latitude,$longitude?q=$latitude,$longitude(label)"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                    intent.setPackage("com.google.android.apps.maps")

                                    if (intent.resolveActivity(packageManager) != null) {
                                        startActivity(intent)
                                    } else {
                                        // Обробка випадку, коли додаток Google Карти не встановлено
                                        // або не знайдено відповідного обробника Intent
                                        // Наприклад, можна відкрити веб-версію Google Карти в браузері.
                                        val webUri = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri))
                                        startActivity(webIntent)
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        val book:Button = findViewById(R.id.button_book)

        if (userId != null) {
            User.getUserByUid(userId){userr->
                if (userr != null) {
                    if(userr.type == "refugee") {
                        book.setOnClickListener(){
                            val chat = Chat()
                            if (apartment != null) {
                                chat.landlord_uid = apartment.uid_poster
                            }
                            chat.refugee_uid = userId
                            chat.saveChatWithCheck(chat.refugee_uid,chat.landlord_uid)
                            if (apartment != null) {
                                User.getUserByUid(apartment.uid_poster){landlord->
                                    val intent = Intent(this@ApartmentActivity, ChatActivity::class.java)
                                    intent.putExtra("user", landlord)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                    else if(userr.type == "landlord")
                    {
                        book.setText("Edit Post")
                        book.setOnClickListener()
                        {
                            val intent = Intent(this@ApartmentActivity, EditApartmentActivity::class.java)
                            intent.putExtra("apartment", apartment)
                            startActivity(intent)
                            finish()
                        }
                    }
                    }
                }
            }

        val title: TextView = findViewById(R.id.textView_title)
        val price: TextView = findViewById(R.id.textView_price)
        val access: TextView = findViewById(R.id.textView_access)
        val description: TextView = findViewById(R.id.textView_description)
        val location: TextView = findViewById(R.id.textView_location)
        val facilietie: MutableList<TextView> = mutableListOf()
        facilietie.add(findViewById(R.id.textView_facilitie1))
        facilietie.add(findViewById(R.id.textView_facilitie2))
        facilietie.add(findViewById(R.id.textView_facilitie3))
        facilietie.add(findViewById(R.id.textView_facilitie4))
        val facilietie_icon: MutableList<ImageView> = mutableListOf()
        facilietie_icon.add(findViewById(R.id.imageView_facilietie1))
        facilietie_icon.add(findViewById(R.id.imageView_facilietie2))
        facilietie_icon.add(findViewById(R.id.imageView_facilietie3))
        facilietie_icon.add(findViewById(R.id.imageView_facilietie4))
        val image:ImageView = findViewById(R.id.imageView1)
        val image_availiable:ImageView = findViewById(R.id.imageView)

        if (apartment != null) {
            title.text = apartment.title
            price.text = apartment.price.toString() + "€"
            if (apartment.access) access.text = "Availiable"
            else
            {
                image_availiable.setImageResource(R.drawable.booked_icon)
                access.text = "Booked"
            }
            description.text = apartment.description
            location.text = apartment.country + ", " + apartment.city

            for (item in facilietie.indices) {
                if (apartment.facilities.size > item && apartment.facilities[item].isNotEmpty()) {
                    facilietie[item].text = apartment.facilities[item]
                    facilietie_icon[item].isVisible = true
                }
            }

            apartment.updateApartmentImage(mAuth,image,this)

        }
    }
}