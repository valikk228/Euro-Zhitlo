package com.example.euro_zhitlo.Refugee

import Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.euro_zhitlo.Account.RegisterActivity
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class RefugeeProfileActivity : AppCompatActivity() {

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_profile_activity)

        val exit: Button = findViewById(R.id.button5)
        val mAuth = FirebaseAuth.getInstance()

        exit.setOnClickListener(){
            mAuth.signOut()
            val intent = Intent(this@RefugeeProfileActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView,3)
    }
}