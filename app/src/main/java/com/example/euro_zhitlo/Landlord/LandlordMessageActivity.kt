package com.example.euro_zhitlo.Landlord

import Navigation
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentAdapter
import com.example.euro_zhitlo.Apartment.ChatAdapter
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.Chat.ChatActivity
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

        setRecyclerView()
    }

    fun setRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Отримати чати для landlord
        val user = mAuth.currentUser
        if (user != null) {
            Chat.getChatsForLandlord(user.uid) { chats ->
                val chatAdapter = ChatAdapter(this, chats)
                chatAdapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
                    override fun onItemClick(chat: Chat) {
                        User.getUserByUid(chat.refugee_uid){userr->
                            val intent = Intent(this@LandlordMessageActivity, ChatActivity::class.java)
                            intent.putExtra("user", userr)
                            startActivity(intent)
                        }
                    }
                })
                // Встановіть адаптер для RecyclerView
                recyclerView.adapter = chatAdapter
            }
        }
    }
}

