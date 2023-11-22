package com.example.euro_zhitlo.Apartment

import Apartment
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.euro_zhitlo.Landlord.LandlordMainActivity
import com.example.euro_zhitlo.Location.City
import com.example.euro_zhitlo.Location.Country
import com.example.euro_zhitlo.Location.GeographyApiHandler
import com.example.euro_zhitlo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.util.UUID

class EditApartmentActivity : AppCompatActivity() {

    private val geographyApiHandler = GeographyApiHandler()
    private var apartment = Apartment()
    private val REQUEST_IMAGE = 1
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var photo: ImageView
    private var imageUrl = ""
    private var selectedImageUri: Uri? = null
    private lateinit var image: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_apartment_activity)

        val choosePhoto: ImageView = findViewById(R.id.imageView13)
        val title: EditText = findViewById(R.id.editTextTitle)
        val price: EditText = findViewById(R.id.editTextPrice)
        val countries: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        val cities: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView2)
        val description: EditText = findViewById(R.id.editTextDescription)
        val facilities: EditText = findViewById(R.id.editTextFacilities)
        val booked: CheckBox = findViewById(R.id.checkBox)
        val save: Button = findViewById(R.id.buttonSave)
        val delete: ImageView = findViewById(R.id.imageViewDelete)

        val back: ImageView = findViewById(R.id.imageView_back)
        back.setOnClickListener(){
            finish()
        }

        val citiesAdapter = ArrayAdapter(
            this@EditApartmentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        cities.setAdapter(citiesAdapter)

        var listOfCountries: List<Country>? = null
        var listOfCities: List<City>? = null
        geographyApiHandler.getAllEuropeanCountries { result ->
            runOnUiThread {
                result?.let { countriesJson ->
                    val europeanCountries =
                        geographyApiHandler.parseCountriesFromJson(countriesJson)
                            .toMutableList()
                    val gson = Gson()
                    listOfCountries =
                        gson.fromJson(countriesJson, Array<Country>::class.java).toList()

                    europeanCountries.removeIf { it == "Russian Federation" }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        europeanCountries
                    )
                    countries.setAdapter(adapter)

                    // Слухач для вибору країни зі списку
                    countries.setOnItemClickListener { _, _, _, _ ->
                        var selectedCountry = ""
                        for (country in listOfCountries!!) {
                            if (country.name == countries.text.toString()) {
                                selectedCountry = country.alpha2code
                            }
                        }

                        if (selectedCountry != null) {
                            // Отримання міст для вибраної країни
                            geographyApiHandler.getCitiesByCountry(selectedCountry) { result ->
                                runOnUiThread {
                                    result?.let { citiesJson ->
                                        val listCities =
                                            geographyApiHandler.parseCitiesFromJson(
                                                citiesJson
                                            ).toMutableList()
                                        val gson = Gson()
                                        listOfCities = gson.fromJson(
                                            citiesJson,
                                            Array<City>::class.java
                                        ).toList()

                                        val citiesAdapter = ArrayAdapter(
                                            this@EditApartmentActivity,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            listCities
                                        )
                                        cities.setAdapter(citiesAdapter)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        apartment = intent.getParcelableExtra<Apartment>("apartment")!!

        photo = findViewById(R.id.imageView12)

        choosePhoto.setOnClickListener() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }
        apartment.updateApartmentImage(mAuth, photo, this)

        title.setText(apartment.title)
        price.setText(apartment.price.toString())
        countries.setText(apartment.country)
        cities.setText(apartment.city)
        description.setText(apartment.description)

        val facilitiesStringBuilder = StringBuilder()
        for ((i, facilitie) in apartment.facilities.withIndex()) {
            if (i != 0) {
                facilitiesStringBuilder.append(",")
            }
            facilitiesStringBuilder.append(facilitie)
        }

        facilities.setText(facilitiesStringBuilder.toString())

        if (!apartment.access) {
            booked.isChecked = true
        }

        save.setOnClickListener() {
            if (selectedImageUri != null) {
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                image = bitmap

                val byteArrayOutputStream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val data = byteArrayOutputStream.toByteArray()

                val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
                val uploadTask: UploadTask = imageRef.putBytes(data)

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val newImageUrl = uri.toString()

                        // Отримати існуючий об'єкт Apartment за uid та іншими умовами
                        // Зверніть увагу, що вам потрібно реалізувати метод getApartmentById
                        apartment.getApartmentByUid(apartment.uid) { result ->
                            if (result != null) {
                                // Оновити посилання на фото в класі Apartment
                                result.image = newImageUrl
                                result.title = title.text.toString()
                                result.price = price.text.toString().toInt()
                                result.country = countries.text.toString()
                                result.city = cities.text.toString()
                                result.access = !booked.isChecked
                                result.description = description.text.toString()
                                result.facilities = facilities.text.toString().split(",")
                                result.saveToDatabase()

                                // Оновити посилання на фото в imageUrl
                                imageUrl = newImageUrl
                            }

                            val intent = Intent(this@EditApartmentActivity, LandlordMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }.addOnFailureListener { exception ->
                    // Обробити помилку завантаження зображення
                }
            } else {
                // Якщо фотографія не вибрана, зберігаємо інші дані без фото
                apartment.getApartmentByUid(apartment.uid) { result ->
                    if (result != null) {
                        result.title = title.text.toString()
                        result.price = price.text.toString().toInt()
                        result.country = countries.text.toString()
                        result.city = cities.text.toString()
                        result.access = !booked.isChecked
                        result.description = description.text.toString()
                        result.facilities = facilities.text.toString().split(",")
                        result.saveToDatabase()
                    }

                    val intent = Intent(this@EditApartmentActivity, LandlordMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        delete.setOnClickListener() {
            // Видалення об'єкта, наприклад, за допомогою методу removeApartmentById (потрібно його реалізувати)
            apartment.removeApartmentById(apartment.uid) { success ->
                if (success) {
                    // Об'єкт видалено успішно, перехід на LandlordMainActivity
                    val intent = Intent(this@EditApartmentActivity, LandlordMainActivity::class.java)
                    startActivity(intent)
                    finish() // Закриття поточної активності, якщо потрібно
                    Toast.makeText(this@EditApartmentActivity, "Post was deleted", Toast.LENGTH_SHORT).show()
                } else {
                    // Обробка помилки видалення, якщо потрібно
                    Toast.makeText(this@EditApartmentActivity, "Error of delete post", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data

            if (selectedImageUri != null) {
                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 25f
                circularProgressDrawable.start()

                // Використовуємо Glide для завантаження фотографії і встановлення кругового прогресу
                Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(circularProgressDrawable)
                    .into(photo)
            }
        }
    }
}
