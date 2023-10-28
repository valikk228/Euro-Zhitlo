package com.example.euro_zhitlo.Landlord

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeMessageActivity
import com.example.euro_zhitlo.Refugee.RefugeeProfileActivity
import com.example.euro_zhitlo.Refugee.RefugeeSearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class LandlordMainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView1)
        bottomNavigationView.menu.getItem(0).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myposts -> {
                    true
                }
                R.id.message -> {
                    val intent = Intent(this, LandlordMessageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, LandlordProfileActivity::class.java)
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
