package com.example.euro_zhitlo.Landlord

import Navigation
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Account.RegisterActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeMessageActivity
import com.example.euro_zhitlo.Refugee.RefugeeProfileActivity
import com.example.euro_zhitlo.Refugee.RefugeeSearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LandlordProfileActivity : AppCompatActivity(){

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_profile_activity)

        val exit: Button = findViewById(R.id.button5)
        val mAuth = FirebaseAuth.getInstance()

        exit.setOnClickListener(){
            mAuth.signOut()
            val intent = Intent(this@LandlordProfileActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showLandlordNavigation(bottomNavigationView,2)

    }
}
