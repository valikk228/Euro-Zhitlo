package com.example.euro_zhitlo.Landlord

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
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView1)
        bottomNavigationView.menu.getItem(2).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myposts -> {
                    val intent = Intent(this, LandlordMainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.message -> {
                    val intent = Intent(this, LandlordMessageActivity::class.java)
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
