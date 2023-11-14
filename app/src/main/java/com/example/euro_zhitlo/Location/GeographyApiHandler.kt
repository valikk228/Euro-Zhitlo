package com.example.euro_zhitlo.Location

import android.util.Log
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson

class GeographyApiHandler() {
    private val apiKey = "QjWCULs47nQXEbRt48YjITbLwMmCP4gv"

    fun getAllEuropeanCountries(callback: (String?) -> Unit) {
        val apiUrl = "https://api.apilayer.com/geo/country/region/Europe"

        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("apikey", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("Countries",responseBody.toString())
                callback(responseBody)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

    fun parseCountriesFromJson(jsonString: String): List<String> {
        val gson = Gson()
        val countries = gson.fromJson(jsonString, Array<Country>::class.java)
        return countries.map { it.name }
    }

    fun getCitiesByCountry(countryName: String, callback: (String?) -> Unit) {
        val apiUrl = "https://api.apilayer.com/geo/country/cities/$countryName"

        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("apikey", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("Cities", responseBody.toString())

                if (response.isSuccessful) {
                    val cities = responseBody
                    callback(responseBody)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

    fun parseCitiesFromJson(jsonString: String): List<String> {
        val gson = Gson()
        val cities = gson.fromJson(jsonString, Array<City>::class.java)
        return cities.map { it.name }
    }


    fun getCityByName(cityName: String, callback: (City?) -> Unit) {
        val apiUrl = "https://api.apilayer.com/geo/city/$cityName"

        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("apikey", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("City", responseBody.toString())

                if (response.isSuccessful) {
                    val city = parseCityFromJson(responseBody ?: "")
                    callback(city)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
        })
    }

    private fun parseCityFromJson(jsonString: String): City? {
        val gson = Gson()
        return gson.fromJson(jsonString, City::class.java)
    }

}


