package com.example.euro_zhitlo.Refugee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class RefugeeMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView1)
        bottomNavigationView.menu.getItem(0).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.search -> {
                    val intent = Intent(this, RefugeeSearchActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.message -> {
                    val intent = Intent(this, RefugeeMessageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, RefugeeProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                // Додайте обробку інших пунктів меню за потреби
                else -> false
            }
        }

    }
}