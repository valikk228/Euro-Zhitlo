package com.example.euro_zhitlo.Landlord

import Apartment
import ApartmentAdapter
import Navigation
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentData
import com.example.euro_zhitlo.Apartment.ChoosePhotoActivity
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LandlordMainActivity : AppCompatActivity(){

    private val navigation = Navigation(this)
    private val mAuth = FirebaseAuth.getInstance()
    private val userUid = mAuth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_activity_main)

        val add:ImageView = findViewById(R.id.imageView_add)
        add.setOnClickListener(){
            val intent = Intent(this@LandlordMainActivity, ChoosePhotoActivity::class.java)
            startActivity(intent)
        }
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showLandlordNavigation(bottomNavigationView,0)

        SetRecyclerView()
    }

    @SuppressLint("SuspiciousIndentation")
    fun SetRecyclerView()
    {
        val read = ApartmentData()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView) // Передпоставимо, що у вас є ListView з ідентифікатором R.id.listView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        if (userUid != null) {
            read.readApartmentsLandlordData(userUid) { apartments ->
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
}

