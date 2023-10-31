package com.example.euro_zhitlo.Landlord

import Navigation
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeMessageActivity
import com.example.euro_zhitlo.Refugee.RefugeeProfileActivity
import com.example.euro_zhitlo.Refugee.RefugeeSearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class LandlordMessageActivity : AppCompatActivity(){

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_message_activity)

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showLandlordNavigation(bottomNavigationView,1)
    }
}
