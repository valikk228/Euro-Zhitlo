package com.example.euro_zhitlo.Apartment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.euro_zhitlo.R

class AddApartmentActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 1
    private var myApartment = ApartmentData()
    private lateinit var image:Bitmap
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

        val choosePhotoButton = findViewById<Button>(com.example.euro_zhitlo.R.id.choosePhotoButton)
        choosePhotoButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Створіть Intent для вибору фотографії з галереї
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE)
            }
        })

        val addApartment:Button = findViewById(R.id.buttonAdd)
        addApartment.setOnClickListener(){
            if(Title.text.toString()!=null && Price.text.toString()!=null && Location.text.toString()!=null
                && Description.text.toString()!=null && Facilities.text.toString()!=null){
                title = Title.text.toString()
                price = Price.text.toString().toInt()
                location = Location.text.toString()
                description = Description.text.toString()
                facilities = Facilities.text.toString().split(",")

                myApartment.uploadData(image,title,price,location,true,description,facilities)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            // Отримайте URI вибраної фотографії
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                val inputStream = contentResolver.openInputStream(selectedImageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                image = bitmap
            }
        }
    }
}