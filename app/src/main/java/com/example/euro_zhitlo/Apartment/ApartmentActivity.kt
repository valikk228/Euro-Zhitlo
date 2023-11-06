package com.example.euro_zhitlo.Apartment

import Apartment
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.Chat.Chat
import com.example.euro_zhitlo.Chat.ChatActivity
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth

class ApartmentActivity: AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val userId = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apartment_activity)

        val back:ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener(){
            finish()
        }

        val apartment = intent.getParcelableExtra<Apartment>("apartment")
        val book:Button = findViewById(R.id.button_book)

        if (userId != null) {
            User.getUserByUid(userId){userr->
                if (userr != null) {
                    if(userr.type == "refugee") {
                        book.setOnClickListener(){
                            val chat = Chat()
                            if (apartment != null) {
                                chat.landlord_uid = apartment.uid
                            }
                            chat.refugee_uid = userId
                            chat.saveChatWithCheck(chat.refugee_uid,chat.landlord_uid)
                            if (apartment != null) {
                                User.getUserByUid(apartment.uid){landlord->
                                    val intent = Intent(this@ApartmentActivity, ChatActivity::class.java)
                                    intent.putExtra("user", landlord)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            book.setText("Edit post")
        }

        val title: TextView = findViewById(R.id.textView_title)
        val price: TextView = findViewById(R.id.textView_price)
        val access: TextView = findViewById(R.id.textView_access)
        val description: TextView = findViewById(R.id.textView_description)
        val location: TextView = findViewById(R.id.textView_location)
        val facilietie: MutableList<TextView> = mutableListOf()
        facilietie.add(findViewById(R.id.textView_facilitie1))
        facilietie.add(findViewById(R.id.textView_facilitie2))
        facilietie.add(findViewById(R.id.textView_facilitie3))
        facilietie.add(findViewById(R.id.textView_facilitie4))
        val facilietie_icon: MutableList<ImageView> = mutableListOf()
        facilietie_icon.add(findViewById(R.id.imageView_facilietie1))
        facilietie_icon.add(findViewById(R.id.imageView_facilietie2))
        facilietie_icon.add(findViewById(R.id.imageView_facilietie3))
        facilietie_icon.add(findViewById(R.id.imageView_facilietie4))
        val image:ImageView = findViewById(R.id.imageView1)
        val image_availiable:ImageView = findViewById(R.id.imageView5)

        if (apartment != null) {
            title.text = apartment.title
            price.text = apartment.price.toString() + "€"
            if (apartment.access) access.text = "Availiable"
            else
            {
                image_availiable.setImageResource(R.drawable.booked_icon)
                access.text = "Booked"
            }
            description.text = apartment.description
            location.text = apartment.location

            for (item in facilietie.indices) {
                if (apartment.facilities.size > item && apartment.facilities[item].isNotEmpty()) {
                    facilietie[item].text = apartment.facilities[item]
                    facilietie_icon[item].isVisible = true
                }
            }


            apartment.getBitmapFromFirebaseStorage { bitmap ->
                if (bitmap != null) {
                    // Встановлюємо Bitmap у ImageView, якщо він доступний
                    image.setImageBitmap(bitmap)
                }
            }
        }
    }
}