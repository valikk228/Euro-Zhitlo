package com.example.euro_zhitlo.Account

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Landlord.LandlordMainActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private val SPLASH_TIME_OUT: Long = 4000 // 2 секунды (в миллисекундах)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        Handler().postDelayed({
            // Здесь создается интент для перехода к MainActivity
            database = FirebaseDatabase.getInstance()
            userRef = database.getReference("typeUser")
            mAuth = FirebaseAuth.getInstance()

            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isAuthenticated = preferences.getBoolean("isAuthenticated", false)

            if (isAuthenticated)
            {
                checkUserRole()
            }
            else
            {
                val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_TIME_OUT)
    }

    private fun checkUserRole() {
        val user = mAuth.currentUser
        if (user != null) {
            User.getUserByUid(user.uid) { userObj ->
                if (userObj != null) {
                    when (userObj.type) {
                        "refugee" -> {
                            val intent = Intent(this@SplashActivity, RefugeeMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        "landlord" -> {
                            val intent = Intent(this@SplashActivity, LandlordMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        else -> {
                            // Якщо роль невідома, перехід на сторінку для вибору ролі
                            val intent = Intent(this@SplashActivity, RoleActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                else
                {
                }
            }
        }
    }
}


