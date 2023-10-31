package com.example.euro_zhitlo.Landlord

import Navigation
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Apartment.AddApartmentActivity
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
    }
}
