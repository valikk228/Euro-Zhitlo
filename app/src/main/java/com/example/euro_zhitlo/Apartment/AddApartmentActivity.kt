package com.example.euro_zhitlo.Apartment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.Landlord.LandlordMainActivity
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth

class AddApartmentActivity : AppCompatActivity() {

    private lateinit var title:String
    private var price:Int = 0
    private lateinit var location:String
    private lateinit var description:String
    private lateinit var facilities:List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_apartment_activity)

        val Title:EditText = findViewById(R.id.editTextTitle)
        val Price:EditText = findViewById(R.id.editTextPrice)
        val Location:EditText = findViewById(R.id.editTextLocation)
        val Description:EditText = findViewById(R.id.editTextDescription)
        val Facilities:EditText = findViewById(R.id.editTextFacilities)

        var imageUrl: String = intent.getStringExtra("imageUrl") ?: ""

        val back: ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener(){
            finish()
        }

        val addApartment: Button = findViewById(R.id.buttonAdd)
        val apartmentData = ApartmentData()

        addApartment.setOnClickListener() {
            if (Title.text.toString() != null && Price.text.toString() != null && Location.text.toString() != null
                && Description.text.toString() != null && Facilities.text.toString() != null
            ) {
                title = Title.text.toString()
                price = Price.text.toString().toInt()
                location = Location.text.toString()
                description = Description.text.toString()
                facilities = Facilities.text.toString().split(",")

                // Отримайте uid користувача з Firebase Authentication
                val currentUser = FirebaseAuth.getInstance().currentUser
                val uid = currentUser?.uid

                if (uid != null) {
                    apartmentData.uploadData(
                        uid,
                        imageUrl,
                        title,
                        price,
                        location,
                        true,
                        description,
                        facilities
                    )
                    val intent = Intent(this@AddApartmentActivity,LandlordMainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // Обробка ситуації, коли uid користувача не знайдено
                }

            }
        }
    }
}