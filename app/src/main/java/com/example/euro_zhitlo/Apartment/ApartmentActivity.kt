package com.example.euro_zhitlo.Apartment

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.R

class ApartmentActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apartment_activity)

        val back:ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener(){
            finish()
        }

    }
}