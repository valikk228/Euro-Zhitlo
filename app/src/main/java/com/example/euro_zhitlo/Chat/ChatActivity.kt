package com.example.euro_zhitlo.Chat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Apartment.MessageAdapter
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class ChatActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val userId = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)


        val nickname: TextView = findViewById(R.id.textView_nickname)
        val image_companion: ImageView = findViewById(R.id.imageView3)
        val image_user: ImageView = findViewById(R.id.imageView5)

        val back:ImageView = findViewById(R.id.imageView_back)
        val send: Button = findViewById(R.id.button_send)

        back.setOnClickListener(){
            finish()
        }

        val companion = intent.getParcelableExtra<User>("user")

        if (userId != null) {
            User.getUserByUid(userId) { userr ->
                if (userr != null) {
                    userr.getBitmapFromFirebaseStorage { image ->
                        image_user.setImageBitmap(image)
                    }
                }
            }
        }

        if (companion != null) {
            nickname.text = companion.nickname
            companion.getBitmapFromFirebaseStorage { image ->
                image_companion.setImageBitmap(image)
            }
        }

        send.setOnClickListener(){
            val text: EditText = findViewById(R.id.editTextText2)
            val message = Message()

            message.text = text.text.toString()
            if (companion != null) {
                message.uid_receiver = companion.uid
            }
            if(user != null) {
                message.uid_sender = user.uid
            }
            message.time = Calendar.getInstance().time
            message.saveToDatabase()
            text.setText("")
        }
        setRecyclerView()
        val messagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                setRecyclerView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обробка помилок взаємодії з Firebase
                Log.e("ChatActivity", "Помилка взаємодії з Firebase: ${databaseError.message}")
            }
        })
    }

    fun setRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        user?.let {
            Message.readMessagesForUser(it.uid){messageList ->

                for(message in messageList) {
                    Log.d("Sender", message.uid_sender)
                }
                val messageAdapter = MessageAdapter(this, messageList)
                recyclerView.adapter = messageAdapter
            }
        }
    }
}

