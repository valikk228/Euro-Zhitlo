package com.example.euro_zhitlo.Refugee

import Apartment
import Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentAdapter
import com.example.euro_zhitlo.Apartment.ApartmentData
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class RefugeeMainActivity : AppCompatActivity() {

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_activity_main)

        val apartmentActivity: Button = findViewById(R.id.button7)
        apartmentActivity.setOnClickListener(){
            val intent = Intent(this, ApartmentActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView,0)


        val read = ApartmentData()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView) // Передпоставимо, що у вас є ListView з ідентифікатором R.id.listView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        read.readApartmentsData { apartments ->
            // Отримуємо дані в цьому зворотньому виклику
            val adapter = ApartmentAdapter(this, apartments)
            recyclerView.adapter = adapter
        }
    }
}