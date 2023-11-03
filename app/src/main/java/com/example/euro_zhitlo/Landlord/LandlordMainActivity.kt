package com.example.euro_zhitlo.Landlord

import Apartment
import Navigation
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.AddApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentAdapter
import com.example.euro_zhitlo.Apartment.ApartmentData
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class LandlordMainActivity : AppCompatActivity(){

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_activity_main)

        val add:ImageView = findViewById(R.id.imageView_add)
        add.setOnClickListener(){
            val intent = Intent(this@LandlordMainActivity,AddApartmentActivity::class.java)
            startActivity(intent)
        }
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showLandlordNavigation(bottomNavigationView,0)

        SetRecyclerView()
    }

    fun SetRecyclerView()
    {
        val read = ApartmentData()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView) // Передпоставимо, що у вас є ListView з ідентифікатором R.id.listView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

            read.readApartmentsLandlordData { apartments ->
                // Отримуємо дані в цьому зворотньому виклику
                val adapter = ApartmentAdapter(this, apartments)
                adapter.setOnItemClickListener(object : ApartmentAdapter.OnItemClickListener {
                    override fun onItemClick(apartment: Apartment) {
                        val intent = Intent(this@LandlordMainActivity, ApartmentActivity::class.java)
                        intent.putExtra("apartment", apartment)
                        startActivity(intent)
                    }
                })
                recyclerView.adapter = adapter
            }
    }
}

