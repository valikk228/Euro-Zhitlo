package com.example.euro_zhitlo.Refugee

import Apartment
import ApartmentAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentData
import com.example.euro_zhitlo.R

class AdvancedSearchActivity:AppCompatActivity() {

    private val apartmentData = ApartmentData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advanced_search_activity)

        val back: ImageView = findViewById(R.id.imageView14)
        back.setOnClickListener(){
            finish()
        }

        val country = intent.getStringExtra("country")
        val priceString = intent.getStringExtra("price")
        var price = if (priceString?.isNotBlank() == true) priceString.toInt() else 0
        if(price == 0 && priceString?.isNotBlank() == true)price = 1
        val availiable = intent.getBooleanExtra("availiable",false)
        val recyclerView:RecyclerView = findViewById(R.id.recyclerView)

        apartmentData.readApartmentsRefugeeData { allApartments ->
            val filteredApartments = allApartments.filter { apartment ->
                (country == "" || apartment.country == country) &&
                (price == 0 || apartment.price <= price) &&
                        ( (availiable && apartment.access) || !availiable)
            }

            var adapter = ApartmentAdapter(this, filteredApartments)
            adapter.setOnItemClickListener(object : ApartmentAdapter.OnItemClickListener {
                override fun onItemClick(apartment: Apartment) {
                    val intent = Intent(this@AdvancedSearchActivity, ApartmentActivity::class.java)
                    val extras = Bundle()
                    extras.putParcelable("apartment", apartment)
                    intent.putExtras(extras)
                    startActivity(intent)
                }
            })
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }
}
