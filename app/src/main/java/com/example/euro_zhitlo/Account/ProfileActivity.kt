package com.example.euro_zhitlo.Account

import Apartment
import ApartmentAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.euro_zhitlo.Apartment.ApartmentActivity
import com.example.euro_zhitlo.Apartment.ApartmentData
import com.example.euro_zhitlo.Chat.ChatActivity
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity: AppCompatActivity() {

    var landlord = User()
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        landlord = intent.getParcelableExtra<User>("landlord")!!

        val back: ImageView = findViewById(R.id.imageView_back2)
        back.setOnClickListener(){
            finish()
        }

        val message: Button = findViewById(R.id.buttonMessage)
        val nickname: TextView = findViewById(R.id.textView_nickname)
        val email: TextView = findViewById(R.id.textView_email)
        val location: TextView = findViewById(R.id.textView_location)
        val phone: TextView = findViewById(R.id.textView_phone)
        val avatar: ImageView = findViewById(R.id.imageView)

        User.getUserByUid(landlord.uid){ user->
            if (user != null) {
                nickname.text = landlord.nickname
                email.text = landlord.email
                location.text = landlord.location
                phone.text = landlord.phone
                if(landlord.image != "") {
                    User.updateProfileImage(mAuth, avatar, landlord.image, this)
                }
                if(landlord.type != "landlord") {
                    message.setOnClickListener() {
                        val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    }
                }
                else{
                    message.visibility = INVISIBLE
                }
                SetRecyclerView(landlord.uid)

            }
        }
    }

    fun SetRecyclerView(userUid:String)
    {
        val read = ApartmentData()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView) // Передпоставимо, що у вас є ListView з ідентифікатором R.id.listView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        if (userUid != null) {
            read.readApartmentsLandlordData(userUid){ apartments ->
                // Отримуємо дані в цьому зворотньому виклику
                val adapter = ApartmentAdapter(this, apartments)
                adapter.setOnItemClickListener(object : ApartmentAdapter.OnItemClickListener {
                    override fun onItemClick(apartment: Apartment) {
                        val intent = Intent(this@ProfileActivity, ApartmentActivity::class.java)
                        intent.putExtra("apartment", apartment)
                        startActivity(intent)
                    }
                })
                recyclerView.adapter = adapter
            }
        }
    }
}