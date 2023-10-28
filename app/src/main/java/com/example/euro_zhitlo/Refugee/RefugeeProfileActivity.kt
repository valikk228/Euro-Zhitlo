package com.example.euro_zhitlo.Refugee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.euro_zhitlo.Account.RegisterActivity
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class RefugeeProfileActivity : AppCompatActivity() {
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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView1)
        bottomNavigationView.menu.getItem(3).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, RefugeeMainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
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
                    true
                }
                // Додайте обробку інших пунктів меню за потреби
                else -> false
            }
        }
    }
}