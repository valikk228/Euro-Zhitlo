package com.example.euro_zhitlo.Refugee

import Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class RefugeeMessageActivity : AppCompatActivity() {

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_message_activity)

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView,2)
    }
}