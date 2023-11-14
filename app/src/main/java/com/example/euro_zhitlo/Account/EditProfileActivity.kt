package com.example.euro_zhitlo.Account

import Navigation
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Landlord.LandlordProfileActivity
import com.example.euro_zhitlo.R
import com.example.euro_zhitlo.Refugee.RefugeeProfileActivity
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : AppCompatActivity(){

    private val navigation = Navigation(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)

        val mAuth = FirebaseAuth.getInstance()

        val nickname: EditText = findViewById(R.id.editTextNickname)
        val email: EditText = findViewById(R.id.editTextEmail)
        val location: EditText = findViewById(R.id.autoCompleteTextView)
        val phone: EditText= findViewById(R.id.editTextPhone)

        val save:Button = findViewById(R.id.buttonSave)

        mAuth.currentUser?.let {
            User.getUserByUid(it.uid){user->
                if (user != null) {
                    nickname.setText(user.nickname)
                    location.setText(user.location)
                    phone.setText(user.phone)

                    save.setOnClickListener(){
                        user.nickname = nickname.text.toString()
                        user.location = location.text.toString()
                        user.phone = phone.text.toString()

                        user.saveToDatabase()
                        checkUserRole(mAuth)
                    }
                }
                email.setText(mAuth.currentUser!!.email)
            }
        }
    }

    private fun checkUserRole(mAuth: FirebaseAuth) {
        val user = mAuth.currentUser
        if (user != null) {
            User.getUserByUid(user.uid) { userObj ->
                if (userObj != null) {
                    when (userObj.type) {
                        "refugee" -> {
                            val intent = Intent(this@EditProfileActivity, RefugeeProfileActivity::class.java)
                            startActivity(intent)
                        }

                        "landlord" -> {
                            val intent = Intent(this@EditProfileActivity, LandlordProfileActivity::class.java)
                            startActivity(intent)
                        }

                        else -> {

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
