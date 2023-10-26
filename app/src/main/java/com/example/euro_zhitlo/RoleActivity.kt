package com.example.euro_zhitlo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RoleActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.role_activity)

        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("typeUser")
        mAuth = FirebaseAuth.getInstance()

        val choose1: Button = findViewById(R.id.button3)
        val choose2: Button = findViewById(R.id.button4)

        val user = mAuth.currentUser
        val uid = user?.uid

        if (uid != null) {
            // Перевіряємо, чи вже є роль користувача в базі даних для цього UID
            userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Роль користувача вже збережена, перенаправляємо на MainActivity
                        val intent = Intent(this@RoleActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Роль користувача ще не збережена, дозволяємо користувачу вибрати роль
                        setupRoleSelection()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обробляємо помилку доступу до бази даних
                }
            })
        }
    }

    private fun setupRoleSelection() {
        val choose1: Button = findViewById(R.id.button3)
        val choose2: Button = findViewById(R.id.button4)

        choose1.setOnClickListener {
            // Отримуємо UID поточного користувача
            val user = mAuth.currentUser
            val uid = user?.uid

            if (uid != null) {
                // Зберігаємо тип користувача у базі даних за використанням UID як ключа
                userRef.child(uid).setValue("refugee")
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        choose2.setOnClickListener {
            // Отримуємо UID поточного користувача
            val user = mAuth.currentUser
            val uid = user?.uid

            if (uid != null) {
                // Зберігаємо тип користувача у базі даних за використанням UID як ключа
                userRef.child(uid).setValue("landlord")
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
