package com.example.euro_zhitlo.Landlord

import Navigation
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Account.EditProfileActivity
import com.example.euro_zhitlo.Account.RegisterActivity
import com.example.euro_zhitlo.Account.User
import com.example.euro_zhitlo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LandlordProfileActivity : AppCompatActivity(){

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landlord_profile_activity)

        val nickname:TextView = findViewById(R.id.textView_nickname)
        val email: TextView = findViewById(R.id.textView_email)
        val location: TextView = findViewById(R.id.textView_location)
        val phone: TextView = findViewById(R.id.textView_phone)
        val avatar: ImageView = findViewById(R.id.imageView)

        val exit: Button = findViewById(R.id.buttonLogout)
        val edit: Button = findViewById(R.id.buttonEdit)

        val mAuth = FirebaseAuth.getInstance()
        val userUid = mAuth.currentUser?.uid

        if (userUid != null) {
            User.getUserByUid(userUid){ user->
                email.text = mAuth.currentUser?.email
                if (user != null) {
                    nickname.text = user.nickname
                    location.text = user.location
                    phone.text = user.phone
                    user.getBitmapFromFirebaseStorage { image->
                        avatar.setImageBitmap(image)
                    }
                }
            }
        }

        edit.setOnClickListener(){
            val intent = Intent(this@LandlordProfileActivity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        exit.setOnClickListener(){
            mAuth.signOut()
            val intent = Intent(this@LandlordProfileActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView1)
        navigation.showLandlordNavigation(bottomNavigationView,2)
    }
}
