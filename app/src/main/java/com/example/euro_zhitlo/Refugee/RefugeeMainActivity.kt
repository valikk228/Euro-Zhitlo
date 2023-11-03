package com.example.euro_zhitlo.Refugee

import Apartment
import Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView, 0)


        val read = ApartmentData()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        read.readApartmentsRefugeeData { apartments ->
            val adapter = ApartmentAdapter(this, apartments)
            adapter.setOnItemClickListener(object : ApartmentAdapter.OnItemClickListener {
                override fun onItemClick(apartment: Apartment) {
                    val intent = Intent(this@RefugeeMainActivity, ApartmentActivity::class.java)
                    intent.putExtra("apartment", apartment)
                    startActivity(intent)
                }
            })
            recyclerView.adapter = adapter
        }
    }
}
