package com.example.euro_zhitlo.Landlord

import Navigation
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.ChatAdapter
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LandlordMessageActivity : AppCompatActivity() {

    private val navigation = Navigation(this)
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_message_activity)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showLandlordNavigation(bottomNavigationView, 1)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Отримати чати для landlord
        val user = mAuth.currentUser
        if (user != null) {
            Chat.getChatsForLandlord(user.uid) { chats ->
                val chatAdapter = ChatAdapter(this, chats)

                // Встановіть адаптер для RecyclerView
                recyclerView.adapter = chatAdapter
            }
        }

    }
}

