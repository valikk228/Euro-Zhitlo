package com.example.euro_zhitlo.Account

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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


            val user = mAuth.currentUser
            val uid = user?.uid

            if (uid != null) {
                // Перевіряємо, чи вже є роль користувача в базі даних для цього UID
                userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Роль користувача вже збережена
                            val role = snapshot.getValue(String::class.java)
                            if (role == "refugee") {
                                val intent = Intent(this@SplashActivity, RefugeeMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (role == "landlord") {
                                val intent = Intent(this@SplashActivity, LandlordMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Обробляємо помилку доступу до бази даних
                    }
                })
            }
            else{
                val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_TIME_OUT)
    }
}


