package com.example.euro_zhitlo.Refugee

import Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Apartment.ChatAdapter
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.Chat.ChatActivity
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class RefugeeMessageActivity : AppCompatActivity() {

    private val navigation = Navigation(this)
    private lateinit var chatAdapter: ChatAdapter
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.refugee_message_activity)

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showRefugeeNavigation(bottomNavigationView,2)

        val search: EditText = findViewById(R.id.editTextText)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не потрібно реагувати на цю подію
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Не потрібно реагувати на цю подію
            }

            override fun afterTextChanged(s: Editable?) {
                // Викликаємо метод для фільтрації при зміні тексту
                chatAdapter.filterChats(s.toString())
            }
        })

        setRecyclerView()
    }

    fun setRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Отримати чати для landlord
        val user = mAuth.currentUser
        if (user != null) {
            Chat.getChatsForRefugee(user.uid) { chats ->
                chatAdapter = ChatAdapter(this, chats)
                chatAdapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
                    override fun onItemClick(chat: Chat) {
                        User.getUserByUid(chat.landlord_uid){userr->
                            val intent = Intent(this@RefugeeMessageActivity, ChatActivity::class.java)
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